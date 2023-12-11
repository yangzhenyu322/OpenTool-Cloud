package com.opentool.ai.tool.domain.vo;

import lombok.Data;

import java.util.List;

/** 一条对话的内容
 * / @Author: ZenSheep
 * / @Date: 2023/12/11 20:55
 */
@Data
public class DialogContent {
    private String text; // 文字内容
    private List<String> imageUrlList; // 图像url列表
}