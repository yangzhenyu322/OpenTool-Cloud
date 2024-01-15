package com.opentool.gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 鉴权（访问授权）管理
 * 3. 权限认证，是否放行
 *
 * @Author: ZenSheep
 * @Date: 2023/12/28 15:37
 */
@Slf4j
@Component
public class ScAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        log.info("ScAuthorizationManager.check()");

        return authentication.map(auth -> {
            //SecurityUserDetails userSecurity = (SecurityUserDetails) auth.getPrincipal();
            String path = authorizationContext.getExchange().getRequest().getURI().getPath();
//            for (GrantedAuthority authority : auth.getAuthorities()) {
//                // 根据用户权限和请求路径判断是否授权（待修改）
//                if (authority.getAuthority().equals("ROLE_USER") && path.contains("/user/normal")) {
//                    return new AuthorizationDecision(true);  // 授予权限访问服务
//                } else if (authority.getAuthority().equals("ROLE_ADMIN") && path.contains("/user/admin")) {
//                    return new AuthorizationDecision(true);
//                }
//            }

            // 鉴权：判断该用户角色是否有访问该接口的权限
//            for (GrantedAuthority authority : auth.getAuthorities()) {
//                if (!"admin".equals(authority.getAuthority())) {
//                    log.info("AuthorizationDecision");
//                    return new AuthorizationDecision(false);  // 鉴权失败：-> ScAuthenticationEntryPoint -> ScAccessDeniedHandler
//                }
//            }

            if (auth.getAuthorities().size() <= 0) {
                return new AuthorizationDecision(false); // 鉴权失败：-> ScAuthenticationEntryPoint -> ScAccessDeniedHandler
            }

            log.info("Authorization Success！");
            return new AuthorizationDecision(true); // 鉴权成功 -> CookieToHeadersFilter设置请求头 -> 访问服务接口
        }).defaultIfEmpty(new AuthorizationDecision(false)); // 无任何权限（未登录） ——> AcAuthenticationEntryPoint(只有这里才仅进入AcAuthenticationEntryPoint，而不进入ScAccessDeniedHandler)
    }
}