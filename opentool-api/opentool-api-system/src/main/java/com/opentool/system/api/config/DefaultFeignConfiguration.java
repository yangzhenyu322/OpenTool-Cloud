package com.opentool.system.api.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/25 13:21
 */
public class DefaultFeignConfiguration {
    /**
     * 日志级别为BASIC
     * @return
     */
    @Bean
    public Logger.Level feignLogLevel() { return Logger.Level.BASIC; }
}
