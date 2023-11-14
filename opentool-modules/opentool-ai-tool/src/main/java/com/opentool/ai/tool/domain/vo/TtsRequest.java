package com.opentool.ai.tool.domain.vo;

import lombok.Data;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/10/31 15:21
 */
@Data
public class TtsRequest {
    //    private String audioFormat; // 音频格式
//    private String fileFormat; // 文件保存格式
//    private String fileName; // 文件命名

    private String text; // 文本
    private String voiceRole; // 声优
    private String style; //风格
    private double styleDegree; // 风格强度
    private String styleRole; // 模仿角色
    private double rate; // 音速
    private double pitch; // 音调

}