package com.opentool.gateway.config;

import com.opentool.gateway.security.ScAuthorizationManager;
import com.opentool.gateway.security.ScSecurityContextRepository;
import com.opentool.gateway.security.filter.CookieToHeadersFilter;
import com.opentool.gateway.security.handler.*;
import com.opentool.gateway.security.permit.ScPermitUrlConfig;
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
    CookieToHeadersFilter cookieToHeadersFilter;

    @Autowired
    ScSecurityContextRepository scSecurityContextRepository;

    @Autowired
    ScAuthorizationManager scAuthorizationManager;

    @Autowired
    ScPermitUrlConfig scPermitUrlConfig;

    @Autowired
    ScAccessDeniedHandler scAccessDeniedHandler;

    @Autowired
    ScAuthenticationEntryPoint scAuthenticationEntryPoint;

    @Autowired
    AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    AuthenticationFailHandler authenticationFailHandler;

    @Autowired
    private LogoutHandler logoutHandler;

    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;

    /**
     *
     * 处理链路：
     *      login : CookieToHeadersFilter -> ScSecurityContextRepository -> ScAuthenticationManager（优先级高于  SecurityUserDetailsService）  -> AuthenticationSuccessHandler/AuthenticationFailHandler
     *      logout: CookieToHeadersFilter -> ScSecurityContextRepository -> LogoutHandler -> LogoutSuccessHandler
     *      未登录进行 url request: CookieToHeadersFilter -> ScSecurityContextRepository -> ScAuthorizationManager -> ScAuthenticationEntryPoint
     *      登录后进行url request: CookieToHeadersFilter -> ScSecurityContextRepository -> ScAuthorizationManager -> CookieToHeadersFilter（子线程, 可以在前面ScSecurityContextRepository更新token并重新设置请求头）-> 服务接口
     *      鉴权失败: CookieToHeadersFilter -> ScSecurityContextRepository -> ScAuthorizationManager -> ScAuthenticationEntryPoint -> ScAccessDeniedHandler
     * @param http
     * @return
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        // 将Cookie写入Http请求头中，SecurityWebFiltersOrder枚举类定义了执行次序
        http.addFilterBefore(cookieToHeadersFilter, SecurityWebFiltersOrder.HTTP_HEADERS_WRITER);

        http.csrf().disable()  // 关闭 csrf 防护, 防止用户无法被认证
                // 存储认证信息
                .securityContextRepository(scSecurityContextRepository)
                //请求拦截处理
                .authorizeExchange(exchange -> exchange // 请求拦截处理
                        .pathMatchers(scPermitUrlConfig.permit()).permitAll() // 默认放开的地址
                        .pathMatchers(HttpMethod.OPTIONS).permitAll() // 放开的请求方法
                        .anyExchange().access(scAuthorizationManager) // 其它的地址走后续验证
                )
                // 登录接口
                .httpBasic()
                .and()
                .formLogin().loginPage("/login") //会进行SecurityUserDetailsService的findByUsername判断是否登录成功
                .authenticationSuccessHandler(authenticationSuccessHandler) //认证成功
                .authenticationFailureHandler(authenticationFailHandler) // 认证失败
                .and()
                .exceptionHandling().authenticationEntryPoint(scAuthenticationEntryPoint) // 未认证访问服务接口处理
                .and()
                .exceptionHandling().accessDeniedHandler(scAccessDeniedHandler) // 授权失败
                .and()
                // 登出接口
                .logout().logoutUrl("/logout")
                .logoutHandler(logoutHandler) // 登出处理
                .logoutSuccessHandler(logoutSuccessHandler);  // 登出成功处理

        return http.build();
    }
}