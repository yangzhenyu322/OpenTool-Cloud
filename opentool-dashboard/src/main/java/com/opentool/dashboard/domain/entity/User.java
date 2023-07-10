package com.opentool.dashboard.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/10 1:08
 */
@Data
@TableName("sys_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long userId;
    private Long roleId;
    private String userName;
    private String nickName;
    private String email;
    private String phoneNumber;
    private char sex;
    private String avatar;
    private String password;
    private char status;
    private String loginIp;
    private Date loginDate;
    private Date createTime;
    private String createBy;
    private String updateBy;
    private Date updateTime;
    private String remark;

}
