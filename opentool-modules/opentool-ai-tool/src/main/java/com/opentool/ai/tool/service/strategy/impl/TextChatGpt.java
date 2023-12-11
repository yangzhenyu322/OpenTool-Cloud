package com.opentool.ai.tool.service.strategy.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.opentool.ai.tool.cache.SseLocalCache;
import com.opentool.ai.tool.domain.entity.ChatLog;
import com.opentool.ai.tool.domain.vo.ChatRequest;
import com.opentool.ai.tool.domain.vo.DialogContent;
import com.opentool.ai.tool.listener.GptTextSSEEventSouceListener;
import com.opentool.ai.tool.mapper.ChatLogMapper;
import com.opentool.ai.tool.service.strategy.IChatGptStrategy;
import com.opentool.ai.tool.utils.ChatGptModelUtils;
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
 * 纯文本对话gpt策略实现类
 * / @Author: ZenSheep
 * / @Date: 2023/10/18 20:23
 */
@Slf4j
@Service
public class TextChatGpt implements IChatGptStrategy {
    @Autowired
    private OpenAiStreamClient openAiStreamClient3_5; // 流式对话：用于与用户chat
    @Autowired
    private OpenAiClient openAiClient3_5; // 阻塞对话：用于总结历史对话
    @Autowired
    private OpenAiStreamClient openAiStreamClient4; // 流式对话：用于与用户chat
    @Autowired
    private OpenAiClient openAiClient4; // 阻塞对话：用于总结历史对话

    @Autowired
    private ChatLogMapper chatLogMapper;
    @Value("${chatgpt.summary.rule}")
    private String rule;

    /**
     * 传输信息
     * @param chatRequest chat params
     * @return 提问结果反馈消息
     */
    @Override
    public String sseChat(ChatRequest chatRequest) {
        if (StrUtil.isBlank(chatRequest.getQuestion())) {
            log.error("[{}]参数异常，msg为null", chatRequest.getUid());
            throw new BaseException("参数异常，msg不能为空~");
        }

        // 获取上下文、总结文本
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", chatRequest.getUid());
        map.put("window_id", chatRequest.getWid());
        List<ChatLog> chatLogs = chatLogMapper.selectByMap(map);
        ChatLog chatLog; // 对话日志
        String messageContext; // 对话存储文本
        String summary; // 总结存储文本

        if (chatLogs.size() > 0) {
            chatLog = chatLogs.get(0);
            messageContext = chatLog.getContent();
            summary = chatLog.getSummary();
        } else {
            log.info("[{}]没有历史窗口，开始创建新的对话窗口~", chatRequest.getUid());
            chatLog = new ChatLog();
            chatLog.setUserId(chatRequest.getUid());
            chatLog.setWindowId(chatRequest.getWid());
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
            messages = JSONUtil.toList(messageContext, Message.class);
            // 取出最近5个对话
            if (messages.size() >= 5 * 2) {
                chatMessages = new ArrayList<>(messages.subList(messages.size() - 5 * 2, messages.size()));
                // 联系上下文
                if (StrUtil.isNotBlank(summary)) {
                    chatMessages.add(0, Message.builder().content(summary).role(Message.Role.ASSISTANT).build());
                }

                if (messages.size() % 5 == 0) {
                    // 历史对话为5的倍数，开始进行总结
                    log.info("历史对话为5的倍数，开始进行上下文总结");
                    summary = summaryHistoryMessages(chatMessages, chatRequest.getModel());
                    log.info("总结完成，持久化summary");
                    chatLog.setSummary(summary);
                    chatLogMapper.updateById(chatLog);
                }
            } else {
                chatMessages.addAll(messages);
            }
        }

        // 存储用户当前问题
        Message currentMessage = Message.builder().content(chatRequest.getQuestion()).role(Message.Role.USER).build();
        messages.add(currentMessage);
        chatMessages.add(currentMessage);

        SseEmitter sseEmitter = (SseEmitter) SseLocalCache.CACHE.get(chatRequest.getUid());
        if (sseEmitter == null) {
            log.info("[{}]获取sse失败,没有创建连接，请重试。", chatRequest.getUid());
            throw new BaseException("[{}]获取sse失败,没有创建连接，请重试~");
        }

        // chat
        log.info("[{}]成功提问：[{}]",chatRequest.getUid(), chatRequest.getQuestion());
//        log.info("Chat-Messages:" + chatMessages.toString());

        GptTextSSEEventSouceListener openAiSseEventSourceListener = new GptTextSSEEventSouceListener(sseEmitter, messages, chatLogMapper, chatLog);
        ChatCompletion completion = ChatCompletion
                .builder()
                .messages(chatMessages)
                // 推荐使用 GPT_3_5_TURBO_1106 或 GPT_4_1106_PREVIEW
                .model(chatRequest.getModel())
                .build();
        try {
            if (ChatGptModelUtils.isGPT3_5(chatRequest.getModel())) {
                openAiStreamClient3_5.streamChatCompletion(completion, openAiSseEventSourceListener);
            } else if (ChatGptModelUtils.isGPT4(chatRequest.getModel())) {
                openAiStreamClient4.streamChatCompletion(completion, openAiSseEventSourceListener);
            }

        } catch (BaseException e) {
            e.printStackTrace();
        }

        return "成功调用open api，开始返回流式响应数据";
    }

    /**
     * 总结历史消息：防止tokens过长
     * @param chatMessages 需要总结的上下文
     * @return 总结文本
     */
    public String summaryHistoryMessages(List<Message> chatMessages, String model) {
        // 满足要求，开始总结
        String summary;
        chatMessages.add(Message.builder().content(rule).role(Message.Role.USER).build());  // 添加总结规则
        // 进行总结
        ChatCompletion chatCompletion = ChatCompletion
                .builder()
                .messages(chatMessages)
                .model(model)
                .build();
        // 开始阻塞
        ChatCompletionResponse chatCompletionResponse = null;
        if (ChatGptModelUtils.isGPT3_5(model)) {
            chatCompletionResponse = openAiClient3_5.chatCompletion(chatCompletion);
        } else if (ChatGptModelUtils.isGPT4(model)) {
            chatCompletionResponse = openAiClient4.chatCompletion(chatCompletion);
        }

        List<ChatChoice> choices = chatCompletionResponse.getChoices();
        summary = choices.get(0).getMessage().getContent();
        chatMessages.remove(chatMessages.size() - 1); // 移除rule

        return summary;
    }

    @Override
    public List<DialogContent> getHistoryList(String uid, String wid) {
        List<DialogContent> dialogContentList = new ArrayList<>();

        Map<String, Object> map = new HashMap<>();
        map.put("user_id", uid);
        map.put("window_id", wid);
        List<ChatLog> chatLogs = chatLogMapper.selectByMap(map);
        if (chatLogs.size() > 0) {
            ChatLog chatLog = chatLogs.get(0);
            // 将List<Message>转换为List<DialogContent>
            List<Message> messages = JSONUtil.toList(chatLog.getContent(), Message.class);
            for (Message message: messages) {
                DialogContent dialogContent = new DialogContent();
                // 只有文本
                dialogContent.setText(message.getContent());
                dialogContentList.add(dialogContent);
            }
            return dialogContentList;
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