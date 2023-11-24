package com.opentool.ai.tool.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/11/22 14:12
 */
public interface ISseService {
    SseEmitter createSee(String uid);

    String closeSee(String uid);
}
