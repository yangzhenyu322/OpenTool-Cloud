package com.opentool.ai.tool;

import com.opentool.common.core.annotation.EnableCustomConfig;
import com.opentool.common.core.annotation.EnableOTFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * AITool服务启动类
 * @author ZenSheep
 */
@EnableCustomConfig
@EnableOTFeignClients
@SpringBootApplication
public class OpenToolAiToolApplication {
    public static void main(String[] args) {
        SpringApplication.run(OpenToolAiToolApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  OpenTool-Ai-Tool业务模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
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