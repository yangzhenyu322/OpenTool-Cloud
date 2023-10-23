package com.opentool.system.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.opentool.system.cache.LocalCache;
import com.opentool.system.cache.MessageLocalCache;
import com.opentool.system.domain.vo.request.ChatRequest;
import com.opentool.system.domain.vo.response.ChatResponse;
import com.opentool.system.listener.OpenAISSEEventSourceListener;
import com.opentool.system.service.IChatService;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.ChatChoice;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * gpt业务类
 * / @Author: ZenSheep
 * / @Date: 2023/10/18 20:23
 */
@Slf4j
@Service
public class ChatService implements IChatService {
    @Autowired
    private OpenAiStreamClient openAiStreamClient; // 流式对话：用于与用户chat
    @Autowired
    private OpenAiClient openAiClient; // 阻塞对话：用于总结历史对话

    /**
     * 创建see连接
     * @param uid
     * @return
     */
    @Override
    public SseEmitter createSee(String uid) {
        // 默认30秒超时，设置0L则永不超时
        SseEmitter sseEmitter = new SseEmitter(0L);
        // 完成后回调
        sseEmitter.onCompletion(() -> {
            log.info("[{}]结束连接...................", uid);
            LocalCache.CACHE.remove(uid);
        });
        // 超时回调
        sseEmitter.onTimeout(() -> {
            log.info("[{}]连接超时...................", uid);
            LocalCache.CACHE.remove(uid);
        });
        // 异常回调
        sseEmitter.onError(throwable -> {
            try {
                log.info("[{}]连接异常,{}", uid, throwable.toString());
                sseEmitter.send(SseEmitter.event()
                        .id(uid)
                        .name("发生异常！")
                        .data(Message.builder().content("发生异常重试！").build())
                        .reconnectTime(3000));
                LocalCache.CACHE.remove(uid);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        try {
            sseEmitter.send(SseEmitter.event().reconnectTime(5000));
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("[{}]创建sse连接成功！", uid);
        LocalCache.CACHE.put(uid, sseEmitter);

        return sseEmitter;
    }

    /**
     * 关闭sse连接
     * @param uid
     */
    @Override
    public void closeSee(String uid) {
        SseEmitter sse = (SseEmitter) LocalCache.CACHE.get(uid);
        if (sse != null) {
            sse.complete();
            // 移除
            LocalCache.CACHE.remove(uid);
        }
    }

    /**
     * 传输信息
     * @param uid
     * @param chatRequest
     * @return
     */
    @Override
    public ChatResponse sseChat(String uid, ChatRequest chatRequest) {
        if (StrUtil.isBlank(chatRequest.getMsg())) {
            log.info("[{}]参数异常，msg为null", uid);
            throw new BaseException("参数异常，msg不能为空~");
        }

        String messageContext = (String) MessageLocalCache.CACHE.get("msg" + uid);
        List<Message> messages = new ArrayList<>();
        if (StrUtil.isNotBlank(messageContext)) {
            messages = JSONUtil.toList(messageContext, Message.class);
            // 尝试进行对话总结
            messages = summaryMessages(messages);
            // 取出最新
            if (messages.size() >= 10) {
                messages = messages.subList(messages.size() - 10, messages.size());  // 获取最近5轮对话：（1条总结，4条完成）
            }

            // 存储用户当前问题
            Message currentMessage = Message.builder().content(chatRequest.getMsg()).role(Message.Role.USER).build();
            messages.add(currentMessage);
        } else {
            Message currentMessages = Message.builder().content(chatRequest.getMsg()).role(Message.Role.USER).build();
            messages.add(currentMessages);
        }

        SseEmitter sseEmitter = (SseEmitter) LocalCache.CACHE.get(uid);

        if (sseEmitter == null) {
            log.info("[{}]聊天消息推送失败:,没有创建连接，请重试。", uid);
            throw new BaseException("[{}]聊天消息推送失败:,没有创建连接，请重试~");
        }

        // chat
        log.info("[{}]成功提问：[{}]",uid, chatRequest.getMsg());
        System.out.println("current newest 5 messages:" + messages.toString());

        OpenAISSEEventSourceListener openAISSEEventSourceListener = new OpenAISSEEventSourceListener(sseEmitter, uid, messages);
        ChatCompletion completion = ChatCompletion
                .builder()
                .messages(messages)
                .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
                .build();
        try {
            openAiStreamClient.streamChatCompletion(completion, openAISSEEventSourceListener);
        } catch (BaseException e) {
            e.printStackTrace();
        }

        ChatResponse response = new ChatResponse();
        response.setQuestionTokens(completion.tokens());

        return response;
    }

    /**
     * 总结历史消息：防止tokens过长
     * @param messages
     * @return
     */
    public List<Message> summaryMessages(List<Message> messages) {
        // 如果总对话轮数大于等于4轮，则把前面3轮对话内容总结成一条
        int summaryLength = 3 * 2; // 总结对话条数 = 轮数 * 2
        int limitLength = 4 * 2; // 存储最大对话条数
        if (messages.size() >= limitLength) {
            List<Message> beforeMessages = messages.subList(0, summaryLength);

            String question = "帮我把以前对话内容进行一个内容总结，要求：如果总对话内容字数超过800，则总结为原来的25%以内；如果总对话内容字数不超过800，则总结为原来的60%以内";
            String answer = "";
            Message questionMessage = Message.builder().content(question).role(Message.Role.USER).build();
            beforeMessages.add(questionMessage);

            // 进行总结
            ChatCompletion chatCompletion = ChatCompletion.builder()
                    .messages(beforeMessages)
                    .build();
            ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletion(chatCompletion); // 开始阻塞
            List<ChatChoice> choices = chatCompletionResponse.getChoices();
            for(ChatChoice chatChoice: choices) {
                answer = chatChoice.getMessage().getContent();
            }
            // 更新缓存历史对话:最终得到一条总结对话 + 一轮最新完整对话
            Message answerMessage = Message.builder().content(answer).role(Message.Role.ASSISTANT).build();
            messages = messages.subList(summaryLength, messages.size());
            messages.add(0, questionMessage);
            messages.add(1, answerMessage);
            System.out.println("Summary Message:" + messages.toString());
        }

        return messages;
    }
}
