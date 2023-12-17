package com.opentool.ai.tool.listener;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.opentool.ai.tool.domain.entity.ChatLog;
import com.opentool.ai.tool.mapper.ChatLogMapper;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.entity.chat.tool.ToolCallFunction;
import com.unfbx.chatgpt.entity.chat.tool.ToolCalls;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/12/12 21:24
 */
@Slf4j
public class ToolCallSSEEventSourceListener extends EventSourceListener {
    private String answer = ""; // 回答文本
    private SseEmitter sseEmitter; // sse连接
    private List<Message> messages; // 纯文本消息
    private List<List<String>>  imageUrlsList; // 图像urls list
    private List<String> imageUrls; // 图像urls
    private ChatLogMapper chatLogMapper;
    private ChatLog chatLog;

    // tool call 相关
    @Getter
    List<ToolCalls> toolCallsList = new ArrayList<>();
    @Getter
    ToolCalls toolCalls = new ToolCalls();
    @Getter
    ToolCallFunction toolCallFunction = ToolCallFunction.builder().name("").arguments("").build();
    final CountDownLatch countDownLatch;


    public ToolCallSSEEventSourceListener(SseEmitter sseEmitter, List<Message> messages, List<List<String>> imageUrlsList, List<String> imageUrls, ChatLogMapper chatLogMapper, ChatLog chatLog, CountDownLatch countDownLatch) {
        this.sseEmitter = sseEmitter;
        this.messages = messages;
        this.imageUrlsList = imageUrlsList;
        this.imageUrls = imageUrls;
        this.chatLogMapper = chatLogMapper;
        this.chatLog = chatLog;
        this.countDownLatch = countDownLatch;
    }

    @SneakyThrows
    @Override
    public void onOpen(EventSource eventSource, Response response) {
        log.info("OpenAI建立sse连接...");
        if (this.imageUrls.size() > 0) {
                // 如果有image，则先传输image再传输文字内容
                Map<String, String> map = new HashMap<>();
                map.put("imageUrlList", this.imageUrls.toString());
                sseEmitter.send(SseEmitter.event()
                        .id("[IMAGES]")
                        .data(map)
                        .reconnectTime(3000));
        }
    }

    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        log.info("OpenAI返回数据：{}", data);
        if (data.equals("[DONE]")) {
            log.info("OpenAI返回数据结束了");
            if (toolCallsList.size() == 0) {
                // 没有发生tool call，传输结束消息给客户端
                // 存储答案
                log.info("开始持久化数据库");
                this.messages.add(Message.builder().content(answer).role(Message.Role.ASSISTANT).build());
                this.imageUrlsList.add(this.imageUrls);
                chatLog.setContent(JSONUtil.toJsonStr(this.messages));
                chatLog.setImageUrls(this.imageUrlsList.toString());
                chatLogMapper.updateById(chatLog);
                log.info("数据库持久化完成");

                Map<String, String> map = new HashMap<>();
                map.put("down", "[DONE]");
                sseEmitter.send(SseEmitter.event()
                        .id("[DONE]")
                        .data(map)
                        .reconnectTime(3000));
                // 传输完成后自动关闭sse
                sseEmitter.complete();
            }

            return;
        }

        ChatCompletionResponse chatCompletionResponse = JSONUtil.toBean(data, ChatCompletionResponse.class);
        Message delta = chatCompletionResponse.getChoices().get(0).getDelta();
        if (CollectionUtil.isNotEmpty(delta.getToolCalls())) {
            toolCallsList.addAll(delta.getToolCalls());
        }

        if (toolCallsList.size() == 0) {
            // 没有发生tool call，传输消息给客户端
            try {
                String curContent = chatCompletionResponse.getChoices().get(0).getDelta().getContent();
                if (curContent != null && curContent.length() > 0) {  // 不能把curContent == "    "忽略，否则代码缩进没了
                    answer += curContent;
                    // 推送消息给客户端
                    sseEmitter.send(SseEmitter.event()
                            .id(chatCompletionResponse.getId())
                            .data(chatCompletionResponse.getChoices().get(0).getDelta())
                            .reconnectTime(3000));
                }
            } catch (Exception e) {
                log.error("sse信息推送失败！");
                eventSource.cancel();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClosed(EventSource eventSource) {
        if(CollectionUtil.isNotEmpty(toolCallsList)){
            // 发生tool call
            toolCalls.setId(toolCallsList.get(0).getId());
            toolCalls.setType(toolCallsList.get(0).getType());
            toolCallsList.forEach(e -> {
                toolCallFunction.setName(e.getFunction().getName());
                toolCallFunction.setArguments(toolCallFunction.getArguments() + e.getFunction().getArguments());
                toolCalls.setFunction(toolCallFunction);
            });
        }

        log.info("OpenAI关闭sse连接...");
        countDownLatch.countDown();
    }

    @SneakyThrows
    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        if(Objects.isNull(response)){
            log.error("OpenAI  sse连接异常:{}", t);
            eventSource.cancel();
            return;
        }
        ResponseBody body = response.body();
        if (Objects.nonNull(body)) {
            log.error("OpenAI  sse连接异常data：{}，异常：{}", body.string(), t);
        } else {
            log.error("OpenAI  sse连接异常data：{}，异常：{}", response, t);
        }
        eventSource.cancel();
    }
}