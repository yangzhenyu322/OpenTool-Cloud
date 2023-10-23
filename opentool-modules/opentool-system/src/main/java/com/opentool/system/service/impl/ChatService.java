package com.opentool.system.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.opentool.system.config.LocalCache;
import com.opentool.system.domain.vo.request.ChatRequest;
import com.opentool.system.domain.vo.response.ChatResponse;
import com.opentool.system.listener.OpenAISSEEventSourceListener;
import com.opentool.system.service.IChatService;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
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
    private OpenAiStreamClient openAiStreamClient;

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
                LocalCache.CACHE.put(uid, sseEmitter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        try {
            sseEmitter.send(SseEmitter.event().reconnectTime(5000));
        } catch (IOException e) {
            e.printStackTrace();
        }
        LocalCache.CACHE.put(uid, sseEmitter);
        log.info("[{}]创建sse连接成功！", uid);
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

        String messageContext = (String) LocalCache.CACHE.get("msg" + uid);
        List<Message> messages = new ArrayList<>();
        if (StrUtil.isNotBlank(messageContext)) {
            messages = JSONUtil.toList(messageContext, Message.class);
            if (messages.size() >= 10) {
                messages = messages.subList(1, 10);
            }
            Message currentMessage = Message.builder().content(chatRequest.getMsg()).role(Message.Role.USER).build();
            messages.add(currentMessage);
        } else {
            Message currentMessages = Message.builder().content(chatRequest.getMsg()).role(Message.Role.USER).build();
            messages.add(currentMessages);
        }

        SseEmitter sseEmitter = (SseEmitter) LocalCache.CACHE.get(uid);

        if (sseEmitter == null) {
            log.info("聊天消息推送失败uid:[{}],没有创建连接，请重试。", uid);
            throw new BaseException("聊天消息推送失败uid:[{}],没有创建连接，请重试~");
        }

        // chat
        log.info("[{}]成功提问：[{}]",uid, chatRequest.getMsg());
        OpenAISSEEventSourceListener openAISSEEventSourceListener = new OpenAISSEEventSourceListener(sseEmitter);
        ChatCompletion completion = ChatCompletion
                .builder()
                .messages(messages)
                .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
                .build();
        openAiStreamClient.streamChatCompletion(completion, openAISSEEventSourceListener);
        LocalCache.CACHE.put("msg" + uid, JSONUtil.toJsonStr(messages), LocalCache.TIMEOUT);
        ChatResponse response = new ChatResponse();
        response.setQuestionTokens(completion.tokens());

        return response;
    }
}
