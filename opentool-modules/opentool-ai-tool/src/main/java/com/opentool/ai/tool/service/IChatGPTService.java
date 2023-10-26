package com.opentool.ai.tool.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/10/18 20:24
 */
public interface IChatGPTService {
    SseEmitter createSee(String uid);

    void closeSee(String uid);

    Long sseChat(String uid, String wid, String msg);

    List<String> getHistoryList(String uid, String wid);

    int cleanHistoryLog(String uid, String wid);
}
