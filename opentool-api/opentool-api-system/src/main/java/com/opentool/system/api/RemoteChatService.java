package com.opentool.system.api;

import com.opentool.system.api.config.DefaultFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Chat Feign: 提供ChatGPT的远程服务
 * / @Author: ZenSheep
 * / @Date: 2023/10/24 20:55
 */
@FeignClient(name = "opentool-ai-tool", contextId = "remote-chat", configuration = DefaultFeignConfiguration.class)
public interface RemoteChatService {
    @GetMapping("/chat/createSse/{uid}")
    SseEmitter createSseConnect(@PathVariable("uid") String uid);

    @GetMapping("/chat/closeSse/{uid}")
    String closeConnect(@PathVariable("uid") String uid);

    @PostMapping("/chat/msg/{uid}")
    Long sseChat(@RequestParam String msg, @PathVariable("uid") String uid);
}