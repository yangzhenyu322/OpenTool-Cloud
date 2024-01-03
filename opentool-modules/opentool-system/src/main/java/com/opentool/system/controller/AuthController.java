package com.opentool.system.controller;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * / @Author: ZenSheep
 * / @Date: 2024/1/2 14:34
 */
@RefreshScope
@RestController
@RequestMapping("/auth")
public class AuthController {
    @GetMapping("/c")
    public String c() {
        return "user center ok c";
    }

    @GetMapping("d")
    public String d() {
        return "user center ok d";
    }
}
