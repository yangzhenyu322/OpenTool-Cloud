package com.opentool.ai.tool.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * STT支持的语言
 * / @Author: ZenSheep
 * / @Date: 2023/11/24 20:05
 */
@Data
@TableName("stt_language")
public class SttLanguage {
    // sst language id
    @TableId(type = IdType.AUTO)
    private Long id;
    private String locate;
    private String language;
}
