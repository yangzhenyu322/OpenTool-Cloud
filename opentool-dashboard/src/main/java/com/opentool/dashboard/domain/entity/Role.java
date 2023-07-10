package com.opentool.dashboard.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/10 18:45
 */
@Data
@TableName("sys_role")
public class Role {
    @TableId(type = IdType.AUTO)
    private Long roleId;
    private String roleName;
    private String roleKey;
    private int roleSort;
    private char dataScope;
    private char status;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
    private String remark;
}
