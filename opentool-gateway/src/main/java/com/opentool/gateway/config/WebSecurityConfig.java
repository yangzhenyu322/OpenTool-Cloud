package com.opentool.gateway.config;

import com.opentool.gateway.handler.ScAccessDeniedHandler;
import com.opentool.gateway.handler.ScAuthenticationEntryPoint;
import com.opentool.gateway.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * WebSecurityConfig 核心配置
 * / @Author: ZenSheep
 * / @Date: 2023/12/28 16:00
 */
@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {
    @Autowired
    ScSecurityContextRepository scSecurityContextRepository;

    @Autowired
    ScAuthenticationManager scAuthenticationManager;

    @Autowired
    ScAuthorizationManager scAuthorizationManager;

    @Autowired
    ScPermitUrlConfig scPermitUrlConfig;

    @Autowired
    ScAccessDeniedHandler scAccessDeniedHandler;

    @Autowired
    ScAuthenticationEntryPoint scAuthenticationEntryPoint;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
         http.csrf().disable()  // 关闭 csrf 防护, 防止用户无法被认证
                .securityContextRepository(scSecurityContextRepository) // 存储认证信息
                .authenticationManager(scAuthenticationManager) // 认证管理
                .authorizeExchange(exchange -> exchange // 请求拦截处理
                        .pathMatchers(scPermitUrlConfig.permit()).permitAll() // 默认放开的地址
                        .pathMatchers(HttpMethod.OPTIONS).permitAll() // 放开的请求方法
                        .anyExchange().access(scAuthorizationManager) // 其它的地址走后续验证
                )
                .addFilterAfter(new ScFilter(), SecurityWebFiltersOrder.AUTHORIZATION) // 拦截处理
                .exceptionHandling().accessDeniedHandler(scAccessDeniedHandler) // 授权失败
                .and()
                .exceptionHandling().authenticationEntryPoint(scAuthenticationEntryPoint); // 认证失败, 考虑合并成一个

        return http.build();
    }
}