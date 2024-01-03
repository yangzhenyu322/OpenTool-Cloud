package com.opentool.system.api.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

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
    public Logger.Level feignLogLevel() { return Logger.Level.BASIC; }
}
