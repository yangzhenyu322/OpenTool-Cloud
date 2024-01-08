package com.opentool.system.api.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户对象
 * @Author: ZenSheep
 * @Date: 2023/7/10 1:08
 */
@Data
@TableName("sys_user")
public class SysUser {
    /** 用户ID **/
    @TableId(type = IdType.AUTO)
    private Long userId;

    /** 角色ID **/
    private Long roleId;

    /** 用户账号 **/
    private String userName;

    /** 用户昵称 **/
    private String nickName;

    /** 邮箱 **/
    private String email;

    /** 手机号码 **/
    private String phoneNumber;

    /** 性别 **/
    private char sex;

    /** 头像地址 **/
    private String avatar;

    /** 密码 **/
    private String password;

    /** 账号状态 **/
    private char status;

    /** 最后登录IP **/
    private String loginIp;

    /** 最后登录时间 **/
    private Date loginDate;

    /** 创建时间 **/
    private Date createTime;

    /** 创建者 **/
    private String createBy;

    /** 更新者 **/
    private String updateBy;

    /** 更新时间 **/
    private Date updateTime;

    /** 备注 **/
    private String remark;
}
