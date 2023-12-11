package com.opentool.general.tool.domain.vo;

import lombok.Data;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/10/12 19:32
 */
@Data
public class ConvertConfigInfo {
    private float quality; // 图片质量
    /** 尺寸转换类型
     *  NoChange: 保持原始图片尺寸，Size: 更改图片尺寸和高度，Width: 仅更改图片宽度，WidthKeepRatio: 改变图片宽度保持长宽比
     *  Height: 仅改变图片高度, HeightKeepRatio: 改变图片高度保持长宽比, Scale：保持长宽比缩放图片
     */
    private String sizeType; // 尺寸转换类型
    private int width; // 宽度
    private int height; // 高度
    private int scale; // 缩放倍率
}