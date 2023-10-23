package com.opentool.system.domain.vo.request;

import lombok.Data;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/10/19 16:01
 */
@Data
public class ChatRequest {
    // 客户端发送的问题参数
    private String msg;
}