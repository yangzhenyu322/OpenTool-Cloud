package com.opentool.gateway;

import com.opentool.common.core.annotation.EnableOTFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/14 13:17
 */
@EnableAsync
@EnableOTFeignClients
@SpringBootApplication
public class OpenToolGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(OpenToolGatewayApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  Gateway网关模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                "   ____                _______          _ \n" +
                "  / __ \\              |__   __|        | |\n" +
                " | |  | |_ __   ___ _ __ | | ___   ___ | |\n" +
                " | |  | | '_ \\ / _ \\ '_ \\| |/ _ \\ / _ \\| |\n" +
                " | |__| | |_) |  __/ | | | | (_) | (_) | |\n" +
                "  \\____/| .__/ \\___|_| |_|_|\\___/ \\___/|_|\n" +
                "        | |                               \n" +
                "        |_|                               \n");
    }
}
