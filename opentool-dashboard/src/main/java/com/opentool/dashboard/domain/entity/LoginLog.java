package com.opentool.dashboard.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


/**
 * 登录日志
 *
 * @author ZenSheep
 * @date 2023/07/09
 */
@Data
@TableName("sys_login_log")
public class LoginLog {
    @TableId(type = IdType.AUTO)
    private Long loginId;
    private Long userId;
    private Date visitsTime;
    private String loginIp;
}
