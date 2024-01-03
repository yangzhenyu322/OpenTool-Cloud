package com.opentool.gateway.security;

import com.opentool.gateway.domain.vto.ScUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * 鉴权（访问授权）管理
 * 3. 权限认证，是否放行
 *
 * / @Author: ZenSheep
 * / @Date: 2023/12/28 15:37
 */
@Slf4j
@Component
public class ScAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        return authentication.map(auth -> {
            ScUser scUser = (ScUser) auth.getPrincipal();
            log.info("ScAuthorizationManager scUser = {}", scUser);

            if (Objects.isNull(scUser)) {
                return new AuthorizationDecision(false);
            }

            return new AuthorizationDecision(true);
        }).defaultIfEmpty(new AuthorizationDecision(false));
    }
}