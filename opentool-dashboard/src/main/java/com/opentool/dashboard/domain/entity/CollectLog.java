package com.opentool.dashboard.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/11 17:53
 */
@Data
@TableName("sys_collect_log")
public class CollectLog {
    @TableId(type = IdType.AUTO)
    private Long collectId;
    private Long userId;
    private Date collectTime;
}
