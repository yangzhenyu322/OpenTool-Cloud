package com.opentool.ai.tool.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opentool.common.core.handler.MyBatisTypeHandler;
import lombok.Data;

import java.util.Date;

/** 对话日志
 * / @Author: ZenSheep
 * / @Date: 2023/10/25 16:00
 */
@Data
@TableName("chat_user_log")
public class ChatLog {
    @TableId(type = IdType.AUTO)
    private Long chatId;
    private String windowId;
    private String userId;
    private Date createTime;
    @TableField(typeHandler = MyBatisTypeHandler.class)
    private String content;
    private String imageUrls;
    private String rule;
    @TableField(typeHandler = MyBatisTypeHandler.class)
    private String summary;
}