package com.opentool.gateway.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Cookie Header 处理
 * / @Author: ZenSheep
 * / @Date: 2024/1/5 16:08
 */
@Slf4j
@Component
public class CookieToHeadersFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("CookieToHeadersFilter.filter()");
        try {
            HttpCookie cookie = exchange.getRequest().getCookies().getFirst("token");
            if (cookie != null) {
                log.info("set request header");
                String token = cookie.getValue();
                ServerHttpRequest request = exchange.getRequest().mutate().header(HttpHeaders.AUTHORIZATION, token).build();

                return chain.filter(exchange.mutate().request(request).build());
            }
        } catch (NotFoundException e) {
            log.error("not found token : {}", e.getMessage());
        }

        return chain.filter(exchange);
    }
}
