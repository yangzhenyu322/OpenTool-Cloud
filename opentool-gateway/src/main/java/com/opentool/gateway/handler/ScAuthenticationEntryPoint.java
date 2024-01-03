package com.opentool.gateway.handler;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * 认证失败执行 Handler
 * / @Author: ZenSheep
 * / @Date: 2023/12/28 15:05
 */
@Slf4j
@Component
public class ScAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().add("Content-Type", "application/json; charset=UTF-8");

        HashMap<String, Object> map = new HashMap<>();
        map.put("code", 403);
        map.put("message", "未认证禁止访问");

        log.error("authentication access forbidden path={}", exchange.getRequest().getPath());

        DataBuffer dataBuffer = response.bufferFactory().wrap(JSON.toJSONBytes(map));
        return response.writeWith(Mono.just(dataBuffer));
    }
}
