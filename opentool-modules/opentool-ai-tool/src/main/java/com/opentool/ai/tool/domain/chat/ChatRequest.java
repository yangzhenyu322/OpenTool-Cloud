package com.opentool.ai.tool.domain.chat;

import lombok.Data;

/**
 * chatgpt 请求参数对象
 * / @Author: ZenSheep
 * / @Date: 2023/10/24 21:43
 */
@Data
public class ChatRequest {
    private String uid;
    private String question;
}
