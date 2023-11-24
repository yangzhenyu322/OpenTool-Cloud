package com.opentool.ai.tool.service.impl;

import com.opentool.ai.tool.cache.SseLocalCache;
import com.opentool.ai.tool.service.ISseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/11/22 14:12
 */
@Slf4j
@Service
public class SseService implements ISseService {
    /**
     * 创建see连接
     * @param uid
     * @return
     */
    @Override
    public SseEmitter createSee(String uid) {
        // 默认30秒超时，设置0L则永不超时
        log.info("[{}]开始创建stt-sse连接", uid);
        SseEmitter sseEmitter = new SseEmitter(0L);
        // 完成后回调
        sseEmitter.onCompletion(() -> {
            log.info("[{}]结束SSE连接...................", uid);
            SseLocalCache.CACHE.remove(uid);
        });
        // 超时回调
        sseEmitter.onTimeout(() -> {
            log.info("[{}]SSE连接超时...................", uid);
            SseLocalCache.CACHE.remove(uid);
        });
        // 异常回调
        sseEmitter.onError(throwable -> {
            log.info("[{}]SSE连接异常:{}", uid, throwable.toString());
//                Map<String, Object> sseDataMap = new HashMap<>();
//                sseDataMap.put("error", uid + "SSE连接异常:" + throwable.toString());
//                sseEmitter.send(SseEmitter.event()
//                        .id("[error]")
//                        .data(sseDataMap)
//                        .reconnectTime(3000));
            SseLocalCache.CACHE.remove(uid);
        });

        try {
            sseEmitter.send(SseEmitter.event().reconnectTime(5000));
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("[{}]创建SSE连接成功！", uid);
        SseLocalCache.CACHE.put(uid, sseEmitter);

        return sseEmitter;
    }

    /**
     * 关闭sse连接
     * @param uid
     */
    @Override
    public String closeSee(String uid) {
        SseEmitter sse = (SseEmitter) SseLocalCache.CACHE.get(uid);
        if (sse != null) {
            log.info("[{}]客户端主动断开SSE，已关闭SSE", uid);
            sse.complete();
            // 移除
            SseLocalCache.CACHE.remove(uid);
        }

        return "[" + uid +  "]关闭SSE连接成功";
    }
}
