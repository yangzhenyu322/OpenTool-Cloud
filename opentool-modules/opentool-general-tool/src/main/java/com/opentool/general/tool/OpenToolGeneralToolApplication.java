package com.opentool.general.tool;

import com.opentool.common.core.annotation.EnableCustomConfig;
import com.opentool.common.core.annotation.EnableOTFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 实用工具业务模块启动类
 * / @Author: ZenSheep
 * / @Date: 2023/8/7 1:42
 */
@EnableCustomConfig
@EnableOTFeignClients
@SpringBootApplication
public class OpenToolGeneralToolApplication {
    public static void main(String[] args) {
        SpringApplication.run(OpenToolGeneralToolApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  OpenTool-General-Tool业务模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
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