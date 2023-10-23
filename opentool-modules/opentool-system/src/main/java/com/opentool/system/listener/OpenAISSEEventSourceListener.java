package com.opentool.system.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** SSE 监听器
 * / @Author: ZenSheep
 * / @Date: 2023/10/19 16:21
 */
@Slf4j
public class OpenAISSEEventSourceListener extends EventSourceListener {
    private long tokens;

    private SseEmitter sseEmitter;

    public OpenAISSEEventSourceListener(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
    }

    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        log.info("OpenAI返回数据: {}", data);
        tokens += 1;
        if (data.equals("[DONE]")) {
            log.info("OpenAI返回数据结束了");

            Map<String, String> map = new HashMap<>();
            map.put("tokens", String.valueOf(tokens()));
            sseEmitter.send(SseEmitter.event()
                    .id("[TOKENS]")
                    .data(map)
                    .reconnectTime(3000));
            map.put("down", "[DONE]");
            sseEmitter.send(SseEmitter.event()
                    .id("[DONE]")
                    .data(map)
                    .reconnectTime(3000));
            // 传输完成后自动关闭sse
            sseEmitter.complete();
            return;
        }

        // ！！！传输消息！！！
        ObjectMapper mapper = new ObjectMapper();
        ChatCompletionResponse completionResponse = mapper.readValue(data, ChatCompletionResponse.class);
        try {
            sseEmitter.send(SseEmitter.event()
                    .id(completionResponse.getId())
                    .data(completionResponse.getChoices().get(0).getDelta())
                    .reconnectTime(3000));
        } catch (Exception e) {
            log.error("sse信息推送失败！");
            eventSource.cancel();
            e.printStackTrace();
        }
    }

    @Override
    public void onClosed(EventSource eventSource) {
        log.info("流式输出返回值总共{}tokens", tokens() - 2);
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

    public long tokens() {
        return tokens;
    }
}