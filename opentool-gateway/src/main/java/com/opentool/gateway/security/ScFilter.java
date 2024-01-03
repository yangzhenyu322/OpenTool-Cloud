package com.opentool.gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;


/**
 * 4. 请求通过后的额外操作处理
 *
 * / @Author: ZenSheep
 * / @Date: 2023/12/28 15:54
 */
@Slf4j
public class ScFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("UserFilter doing... path={}", exchange.getRequest().getPath());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!Objects.isNull(authentication)) {
            Object principal = authentication.getPrincipal();
            log.info("UserFilter doing principal={}", principal);
        }

        return chain.filter(exchange);
    }
}
