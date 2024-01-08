package com.opentool.system.api.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 角色表
 * @Author: ZenSheep
 * @Date: 2023/7/10 18:45
 */
@Data
@TableName("sys_role")
public class SysRole {
    /** 角色ID **/
    @TableId(type = IdType.AUTO)
    private Long roleId;

    /** 角色名称 **/
    private String roleName;

    /** 角色权限字符串 **/
    private String roleKey;

    /** 显示顺序 **/
    private int roleSort;

    /** 数据范围 **/
    private char dataScope;

    /** 创建者 **/
    private String createBy;

    /** 创建时间 **/
    private Date createTime;

    /** 更新者 **/
    private String updateBy;

    /** 更新时间 **/
    private Date updateTime;

    /** 备注 **/
    private String remark;
}
