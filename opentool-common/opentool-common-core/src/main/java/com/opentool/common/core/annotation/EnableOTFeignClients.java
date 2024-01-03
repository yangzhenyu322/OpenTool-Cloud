package com.opentool.common.core.annotation;

import org.springframework.cloud.openfeign.EnableFeignClients;

import java.lang.annotation.*;

/**
 * 自定义feign注解: Enable OpenTool Feign Client
 * 添加basePackages路径
 * / @Author: ZenSheep
 * / @Date: 2023/7/24 21:56
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableFeignClients
public @interface EnableOTFeignClients {
    String[] value() default {};

    String[] basePackages() default { "com.opentool" };

    Class<?>[] basePackageClasses() default {};

    Class<?>[] defaultConfiguration() default {};

    Class<?>[] clients() default {};
}
