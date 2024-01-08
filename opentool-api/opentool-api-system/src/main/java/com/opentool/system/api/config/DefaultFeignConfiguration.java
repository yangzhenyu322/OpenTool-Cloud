package com.opentool.system.api.config;

import feign.Logger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.stream.Collectors;

/**
 * Feign 配置
 * / @Author: ZenSheep
 * / @description: 千万不要添加@Configuration,不然会被作为全局配置文件共享
 * / @Date: 2023/7/25 13:21
 */
public class DefaultFeignConfiguration {
    /**
     * 日志级别为BASIC
     * @return
     */
    @Bean
    public Logger.Level feignLogLevel() { return Logger.Level.FULL; }

    /**
     * 注册HttpMessageConverters对象，在基于WebFulx的Gateway中该Bean不会自动注入
     * @param converters
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
        return new HttpMessageConverters(converters.orderedStream().collect(Collectors.toList()));
    }
}
