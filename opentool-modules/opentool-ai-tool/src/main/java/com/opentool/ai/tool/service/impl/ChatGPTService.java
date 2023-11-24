package com.opentool.ai.tool.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.opentool.ai.tool.cache.SseLocalCache;
import com.opentool.ai.tool.domain.entity.ChatLog;
import com.opentool.ai.tool.listener.OpenAISSEEventSourceListener;
import com.opentool.ai.tool.mapper.ChatLogMapper;
import com.opentool.ai.tool.service.IChatGPTService;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.ChatChoice;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;

/**
 * gpt业务类
 * / @Author: ZenSheep
 * / @Date: 2023/10/18 20:23
 */
@Slf4j
@Service
public class ChatGPTService implements IChatGPTService {
    @Autowired
    private OpenAiStreamClient openAiStreamClient; // 流式对话：用于与用户chat
    @Autowired
    private OpenAiClient openAiClient; // 阻塞对话：用于总结历史对话

    @Autowired
    private ChatLogMapper chatLogMapper;
    @Value("${chatgpt.summary.rule}")
    private String rule;

    /**
     * 传输信息
     * @param uid
     * @param msg
     * @return
     */
    @Override
    public Long sseChat(String uid, String wid, String msg) {
        if (StrUtil.isBlank(msg)) {
            log.error("[{}]参数异常，msg为null", uid);
            throw new BaseException("参数异常，msg不能为空~");
        }

        // 获取上下文、总结文本
        String messageContext;
        String summary;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("user_id", uid);
        map.put("window_id", wid);
        List<ChatLog> chatLogs = chatLogMapper.selectByMap(map);
        ChatLog chatLog;

        if (chatLogs.size() > 0) {
            chatLog = chatLogs.get(0);
            messageContext = chatLog.getContent();
            summary = chatLog.getSummary();
        } else {
            log.info("[{}]没有历史窗口，开始创建新的对话窗口~", uid);
            chatLog = new ChatLog();
            chatLog.setUserId(uid);
            chatLog.setWindowId(wid);
            chatLog.setCreateTime(new Date());
            chatLog.setContent(null);
            chatLog.setRule(rule);
            chatLog.setSummary("");
            chatLogMapper.insert(chatLog);
            log.info("新的对话窗口创建成功");

            messageContext = null;
            summary = null;
        }

        // 历史对话列表
        List<Message> messages = new ArrayList<>();  // 原始对话
        List<Message> chatMessages = new ArrayList<>(); // 总结对话
        if (StrUtil.isNotBlank(messageContext)) {
//            System.out.println("messageContext:" + messageContext);
            messages = JSONUtil.toList(messageContext, Message.class);
            // 取出最近3个对话
            if (messages.size() >= 3 * 2) {
                chatMessages = new ArrayList<>(messages.subList(messages.size() - 3 * 2, messages.size()));
                // 联系上下文
                if (StrUtil.isNotBlank(summary)) {
                    chatMessages.add(0, Message.builder().content(summary).role(Message.Role.ASSISTANT).build());
                }

                if (messages.size() % 3 == 0) {
                    // 历史对话为3的倍数，开始进行总结
                    log.info("历史对话为3的倍数，开始进行上下文总结");
                    summary = summaryHistoryMessages(chatMessages);
                    log.info("总结完成，持久化summary");
                    chatLog.setSummary(summary);
                    chatLogMapper.updateById(chatLog);
                }
            } else {
                for (int i = 0; i < messages.size(); i++) {
                    chatMessages.add(messages.get(i));
                }
            }
        }

        // 存储用户当前问题
        Message currentMessage = Message.builder().content(msg).role(Message.Role.USER).build();
        messages.add(currentMessage);
        chatMessages.add(currentMessage);

        SseEmitter sseEmitter = (SseEmitter) SseLocalCache.CACHE.get(uid);
        if (sseEmitter == null) {
            log.info("[{}]获取sse失败,没有创建连接，请重试。", uid);
            throw new BaseException("[{}]获取sse失败,没有创建连接，请重试~");
        }

        // chat
        log.info("[{}]成功提问：[{}]",uid, msg);
        log.info("Chat-Messages:" + chatMessages.toString());

        OpenAISSEEventSourceListener openAISSEEventSourceListener = new OpenAISSEEventSourceListener(sseEmitter, uid, messages, chatLogMapper, chatLog);
        ChatCompletion completion = ChatCompletion
                .builder()
                .messages(chatMessages)
                .model(ChatCompletion.Model.GPT_3_5_TURBO_16K.getName())
                .build();
        try {
            openAiStreamClient.streamChatCompletion(completion, openAISSEEventSourceListener);
        } catch (BaseException e) {
            e.printStackTrace();
        }

        return completion.tokens();
    }

    /**
     * 总结历史消息：防止tokens过长
     * @param chatMessages
     * @return
     */
    public String summaryHistoryMessages(List<Message> chatMessages) {
        // 满足要求，开始总结
        String summary = "";
        chatMessages.add(Message.builder().content(rule).role(Message.Role.USER).build());  // 添加总结规则
        // 进行总结
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .messages(chatMessages)
                .build();
        ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletion(chatCompletion); // 开始阻塞
        List<ChatChoice> choices = chatCompletionResponse.getChoices();
        summary = choices.get(0).getMessage().getContent();
        chatMessages.remove(chatMessages.size() - 1); // 移除rule

        return summary;
    }


    @Override
    public List<String> getHistoryList(String uid, String wid) {
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", uid);
        map.put("window_id", wid);
        List<ChatLog> chatLogs = chatLogMapper.selectByMap(map);
        if (chatLogs.size() > 0) {
            ChatLog chatLog = chatLogs.get(0);
            // 将List<Message>转换为List<String>
            List<String> historys = new ArrayList<>();
            List<Message> messages = JSONUtil.toList(chatLog.getContent(), Message.class);
            for (Message message: messages) {
                historys.add(message.getContent());
            }

            return historys;
        }

        return null;
    }

    @Override
    public int cleanHistoryLog(String uid, String wid) {
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", uid);
        map.put("window_id", wid);
        int result = chatLogMapper.deleteByMap(map);
        log.info(result > 0 ? "清除历史窗口成功":"清除失败，历史窗口不存在");
        return result;
    }
}