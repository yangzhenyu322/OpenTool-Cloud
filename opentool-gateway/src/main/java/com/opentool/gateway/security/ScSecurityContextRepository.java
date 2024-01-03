package com.opentool.gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 认证信息存储管理
 * 1. 把header拿到的token放入AuthenticationToken
 *
 * / @Author: ZenSheep
 * / @Date: 2023/12/28 15:16
 */
@Slf4j
@Component
public class ScSecurityContextRepository implements ServerSecurityContextRepository {
    @Autowired
    ScAuthenticationManager scAuthenticationManager;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        // authorization: 批准、授权
        String authorization = request.getHeaders().getFirst("Authorization");
        log.info("ScSecurityContextRepository authorization = {}", authorization);

        return scAuthenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(authorization, null))
                .map(SecurityContextImpl::new);
    }
}