package com.opentool.ai.tool.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/** TTS风格角色类
 * / @Author: ZenSheep
 * / @Date: 2023/11/14 21:14
 */
@Data
@TableName("tts_style_role")
public class TtsStyleRole {
    // 风格角色id
    @TableId("style_role_id")
    private Long styleRoleId;
    // 风格角色名
    private String name;
    // 风格描述
    private String description;
}
