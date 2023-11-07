package com.opentool.ai.tool.domain.vo;

import lombok.Data;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/10/31 15:21
 */
@Data
public class TtsRequest {
    private String text; // 文本
    private String voiceRole; // 声优
//    private String audioFormat; // 音频格式
//    private String fileFormat; // 文件保存格式
}