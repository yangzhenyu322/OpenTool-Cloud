package com.opentool.system;

import com.opentool.common.core.annotation.EnableCustomConfig;
import com.opentool.common.core.annotation.EnableOTFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/24 20:15
 */
@EnableOTFeignClients
@EnableCustomConfig
@SpringBootApplication
public class OpenToolSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(OpenToolSystemApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  OpenTool-System系统模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
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