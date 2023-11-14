package com.opentool.ai.tool.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/** TTS语音类型类
 * / @Author: ZenSheep
 * / @Date: 2023/11/14 21:00
 */
@Data
@TableName("tts_style")
public class TtsStyle {
    // 类型ID
    @TableId(type = IdType.AUTO)
    private Long styleId;
    // 角色名
    private String name;
    // 描述
    private String description;
}
