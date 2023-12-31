package com.opentool.ai.tool.listener;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opentool.ai.tool.domain.entity.ChatLog;
import com.opentool.ai.tool.mapper.ChatLogMapper;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/12/9 15:43
 */
@Slf4j
public class GptTextSSEEventSouceListener extends EventSourceListener {
    private String answer = ""; // 回答文本
    private SseEmitter sseEmitter; // sse连接
    private List<Message> messages; // 存文本消息
    private ChatLogMapper chatLogMapper;
    private ChatLog chatLog;

    public GptTextSSEEventSouceListener(SseEmitter sseEmitter, List<Message> messages, ChatLogMapper chatLogMapper, ChatLog chatLog) {
        this.sseEmitter = sseEmitter;
        this.messages = messages;
        this.chatLogMapper = chatLogMapper;
        this.chatLog = chatLog;
    }

    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        log.info("OpenAI返回数据: {}", data);
        if (data.equals("[DONE]")) {
            log.info("OpenAI返回数据结束了");
            // 存储答案
            log.info("开始持久化数据库");
            this.messages.add(Message.builder().content(answer).role(Message.Role.ASSISTANT).build());
            chatLog.setContent(JSONUtil.toJsonStr(messages));
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
            return;
        }

        // 向用户传输消息
        ObjectMapper mapper = new ObjectMapper();
        ChatCompletionResponse completionResponse = mapper.readValue(data, ChatCompletionResponse.class);
        try {
            String curContent = completionResponse.getChoices().get(0).getDelta().getContent();
            if (curContent != null && curContent.length() > 0) {  // 不能把curContent == "    "忽略，否则代码缩进没了
                answer += curContent;
                // 推送消息给客户端
                sseEmitter.send(SseEmitter.event()
                        .id(completionResponse.getId())
                        .data(completionResponse.getChoices().get(0).getDelta())
                        .reconnectTime(3000));
            }
        } catch (Exception e) {
            log.error("sse信息推送失败！");
            eventSource.cancel();
            e.printStackTrace();
        }
    }

    @Override
    public void onClosed(EventSource eventSource) {
        log.info("OpenAI关闭sse连接...");
    }

    @SneakyThrows
    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        if (Objects.isNull(response)) {
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
