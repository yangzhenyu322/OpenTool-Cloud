package com.opentool.ai.tool.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * chatgpt 请求参数对象
 * / @Author: ZenSheep
 * / @Date: 2023/10/24 21:43
 */
@Data
public class ChatRequest {
    private String uid; // user id
    private String wid; // window id
    private String model; // chat model
    private String question; // question text
    private List<String> imgList; // question url list
}
