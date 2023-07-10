package com.opentool.dashboard;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ZenSheep
 * @date 2023/07/09
 */
@SpringBootApplication
@MapperScan("com.opentool.dashboard.mapper")
public class OpenToolDashBoardApplication {
    public static void main(String[] args) {
        SpringApplication.run(OpenToolDashBoardApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  DashBoard服务模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
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