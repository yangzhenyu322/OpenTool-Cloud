package com.opentool.ai.tool.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * TTS语音角色实体
 * / @Author: ZenSheep
 * / @Date: 2023/10/31 15:11
 */
@Data
@TableName("tts_role")
public class TtsRole {
    // 角色ID
    @TableId(type = IdType.AUTO)
    private Long roleId;
    // 角色名
    private String role;
    // 性别
    private String gender;
    // 区域设置
    private String locate;
    // 语言
    private String language;
}
