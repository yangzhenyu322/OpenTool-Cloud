package com.opentool.system.service;

import com.opentool.system.domain.vo.request.ChatRequest;
import com.opentool.system.domain.vo.response.ChatResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/10/18 20:24
 */
public interface IChatService {
    SseEmitter createSee(String uid);

    void closeSee(String uid);

    ChatResponse sseChat(String uid, ChatRequest chatRequest);
}
