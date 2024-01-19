package com.opentool.system.domain.dto;

import com.opentool.common.core.enums.UserRole;
import com.opentool.common.core.enums.UserSex;
import com.opentool.common.core.enums.UserStatus;
import com.opentool.common.core.utils.date.DateUtils;
import com.opentool.system.api.domain.SysUser;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/20 1:58
 */
@Data
public class UserInfo {
    private Long userId;
    private String role;
    private String userName;
    private String nickName;
    private String email;
    private String phoneNumber;
    private String sex;
    private String avatar;
    private String password;
    private String status;
    private String loginIp;
    private String loginDate;
    private String createTime;
    private String createBy;
    private String updateBy;
    private String updateTime;
    private String remark;

    private String repeatPassword;
    private String codeRequestId;
    private String inputCode;

    // User 转换为 UserInfo
    public static List<UserInfo> userConvertUserInfoList(List<SysUser> sysUsers) {
        List<UserInfo> userInfos = new ArrayList<>();

        for(SysUser sysUser : sysUsers) {
            UserInfo userInfo = new UserInfo();

            userInfo.setUserId(sysUser.getUserId());
            if (UserRole.ADMIN.getCode().equals(sysUser.getRoleId())) {
                userInfo.setRole(UserRole.ADMIN.getRole());
            }
            if (UserRole.VIP.getCode().equals(sysUser.getRoleId())) {
                userInfo.setRole(UserRole.VIP.getRole());
            }
            if (UserRole.COMMON.getCode().equals(sysUser.getRoleId())) {
                userInfo.setRole(UserRole.COMMON.getRole());
            }
            userInfo.setUserName(sysUser.getUserName());
            userInfo.setNickName(sysUser.getNickName());
            userInfo.setEmail(sysUser.getEmail());
            userInfo.setPhoneNumber(sysUser.getPhoneNumber());
            if (UserSex.MALE.getCode() == sysUser.getSex()) {
                userInfo.setSex(UserSex.MALE.getSex());
            }
            if (UserSex.WOMAN.getCode() == sysUser.getSex()) {
                userInfo.setSex(UserSex.WOMAN.getSex());
            }
            userInfo.setAvatar(sysUser.getAvatar());
            userInfo.setPassword(sysUser.getPassword());
            if (UserStatus.NORMAL.getCode() == sysUser.getStatus()) {
                userInfo.setStatus(UserStatus.NORMAL.getStatus());
            }
            if (UserStatus.DISABLE.getCode() == sysUser.getStatus()) {
                userInfo.setStatus(UserStatus.DISABLE.getStatus());
            }
            if (UserStatus.DELETE.getCode() == sysUser.getStatus()) {
                userInfo.setStatus(UserStatus.DELETE.getStatus());
            }
            userInfo.setLoginIp(sysUser.getLoginIp());
            userInfo.setLoginDate(DateUtils.DateParseTime(sysUser.getLoginDate()));
            userInfo.setCreateTime(DateUtils.DateParseTime(sysUser.getCreateTime()));
            userInfo.setCreateBy(sysUser.getCreateBy());
            userInfo.setUpdateBy(sysUser.getUpdateBy());
            userInfo.setUpdateTime(DateUtils.DateParseTime(sysUser.getUpdateTime()));
            userInfo.setRemark(sysUser.getRemark());

            userInfos.add(userInfo);
        }

        return userInfos;
    }

    public static SysUser userInfoParseUser(UserInfo userInfo) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userInfo.getUserId());
        if (UserRole.ADMIN.getRole().equals(userInfo.getRole())) {
            sysUser.setRoleId(UserRole.ADMIN.getCode());
        }
        if (UserRole.VIP.getRole().equals(userInfo.getRole())) {
            sysUser.setRoleId(UserRole.VIP.getCode());
        }
        if (UserRole.COMMON.getRole().equals(userInfo.getRole())) {
            sysUser.setRoleId(UserRole.COMMON.getCode());
        }
        sysUser.setUserName(userInfo.getUserName());
        sysUser.setNickName(userInfo.getNickName());
        sysUser.setEmail(userInfo.getEmail());
        sysUser.setPhoneNumber(userInfo.getPhoneNumber());
        if (UserSex.MALE.getSex().equals(userInfo.getSex())) {
            sysUser.setSex(UserSex.MALE.getCode());
        }
        if (UserSex.WOMAN.getSex().equals(userInfo.getSex())) {
            sysUser.setSex(UserSex.WOMAN.getCode());
        }
        sysUser.setAvatar(userInfo.getAvatar());
        sysUser.setPassword(userInfo.getPassword());
        if (UserStatus.NORMAL.getStatus().equals(userInfo.getStatus())) {
            sysUser.setStatus(UserStatus.NORMAL.getCode());
        }
        if (UserStatus.DISABLE.getStatus().equals(userInfo.getStatus())) {
            sysUser.setStatus(UserStatus.DISABLE.getCode());
        }
        if (UserStatus.DELETE.getStatus().equals(userInfo.getStatus())) {
            sysUser.setStatus(UserStatus.DELETE.getCode());
        }
        sysUser.setLoginIp(userInfo.getLoginIp());
        sysUser.setLoginDate(DateUtils.TimeParseDate(userInfo.getLoginDate()));
        sysUser.setCreateTime(DateUtils.TimeParseDate(userInfo.getCreateTime()));
        sysUser.setCreateBy(userInfo.getCreateBy());
        sysUser.setUpdateBy(userInfo.updateBy);
        sysUser.setUpdateTime(DateUtils.TimeParseDate(userInfo.getUpdateTime()));
        sysUser.setRemark(userInfo.getRemark());

        return sysUser;
    }
}
