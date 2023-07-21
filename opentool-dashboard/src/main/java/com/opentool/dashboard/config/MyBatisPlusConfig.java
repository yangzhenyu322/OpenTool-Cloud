package com.opentool.dashboard.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/19 21:24
 */
@Configuration
public class MyBatisPlusConfig {
    /**
     * 实现Mybatis-Plus分页查询配置
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mi = new MybatisPlusInterceptor();
        mi.addInnerInterceptor(new PaginationInnerInterceptor());

        return mi;
    }
}
