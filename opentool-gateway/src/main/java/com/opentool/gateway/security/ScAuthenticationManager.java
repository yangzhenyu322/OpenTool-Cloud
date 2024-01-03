package com.opentool.gateway.security;

import com.opentool.gateway.domain.vto.ScUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Date;

/**
 * 登录认证管理
 * 2. 从AuthenticationToken读取Token并做用户数据解析
 *
 * / @Author: ZenSheep
 * / @Date: 2023/12/28 15:19
 */
@Slf4j
@Component
public class ScAuthenticationManager implements ReactiveAuthenticationManager {
    /**
     * 认证
     * @param authentication
     * @return
     */
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String tokenString = (String) authentication.getPrincipal();

        // 校验token
        ScUser scUser = parseToken(tokenString);
        log.info("ScAuthenticationManager scUser = {}", scUser);

        return Mono.just(authentication).map(auth -> {
            return new UsernamePasswordAuthenticationToken(scUser, null, null);
        });
    }

    /**
     * 校验token
     * @param tokenString
     * @return
     */
    private ScUser parseToken(String tokenString) {
        // 读取token
        String jwtToken = getJwtToken(tokenString);
        log.info("ScAuthenticationManager jwtToken = {}", jwtToken);

        // 模拟认证成功: 这里以后要替换成更加安全的jwt认证方法
        if (StringUtils.hasText(jwtToken) && jwtToken.startsWith("a")) {
            return new ScUser().setId(100001L).setName("ZenSheep").setCreateTime(new Date());
        }

        return null;
    }

    /**
     * 读取Jwt Token
     * @param tokenString
     * @return
     */
    private String getJwtToken(String tokenString) {
        if (!StringUtils.hasText(tokenString)) {
            return null;
        }

        boolean valid = tokenString.startsWith("Bearer ");
        if (!valid) {
            return null;
        }

        return tokenString.replace("Bearer ", "");
    }
}
