package com.opentool.dashboard.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/11 18:16
 */
@Data
@TableName("sys_contribute_log")
public class ContributeLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Date contributeTime;
    private String description;
}
