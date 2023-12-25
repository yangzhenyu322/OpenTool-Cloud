package com.opentool.system.domain.dto;

import com.opentool.common.core.enums.UserRole;
import com.opentool.common.core.enums.UserSex;
import com.opentool.common.core.enums.UserStatus;
import com.opentool.common.core.utils.date.DateUtils;
import com.opentool.system.api.domain.User;
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

    // User 转换为 UserInfo
    public static List<UserInfo> userConvertUserInfoList(List<User> users) {
        List<UserInfo> userInfos = new ArrayList<>();

        for(User user: users) {
            UserInfo userInfo = new UserInfo();

            userInfo.setUserId(user.getUserId());
            if (UserRole.ADMIN.getCode().equals(user.getRoleId())) {
                userInfo.setRole(UserRole.ADMIN.getRole());
            }
            if (UserRole.VIP.getCode().equals(user.getRoleId())) {
                userInfo.setRole(UserRole.VIP.getRole());
            }
            if (UserRole.COMMON.getCode().equals(user.getRoleId())) {
                userInfo.setRole(UserRole.COMMON.getRole());
            }
            userInfo.setUserName(user.getUserName());
            userInfo.setNickName(user.getNickName());
            userInfo.setEmail(user.getEmail());
            userInfo.setPhoneNumber(user.getPhoneNumber());
            if (UserSex.MALE.getCode() == user.getSex()) {
                userInfo.setSex(UserSex.MALE.getSex());
            }
            if (UserSex.WOMAN.getCode() == user.getSex()) {
                userInfo.setSex(UserSex.WOMAN.getSex());
            }
            userInfo.setAvatar(user.getAvatar());
            userInfo.setPassword(user.getPassword());
            if (UserStatus.NORMAL.getCode() == user.getStatus()) {
                userInfo.setStatus(UserStatus.NORMAL.getStatus());
            }
            if (UserStatus.DISABLE.getCode() == user.getStatus()) {
                userInfo.setStatus(UserStatus.DISABLE.getStatus());
            }
            if (UserStatus.DELETE.getCode() == user.getStatus()) {
                userInfo.setStatus(UserStatus.DELETE.getStatus());
            }
            userInfo.setLoginIp(user.getLoginIp());
            userInfo.setLoginDate(DateUtils.DateParseTime(user.getLoginDate()));
            userInfo.setCreateTime(DateUtils.DateParseTime(user.getCreateTime()));
            userInfo.setCreateBy(user.getCreateBy());
            userInfo.setUpdateBy(user.getUpdateBy());
            userInfo.setUpdateTime(DateUtils.DateParseTime(user.getUpdateTime()));
            userInfo.setRemark(user.getRemark());

            userInfos.add(userInfo);
        }

        return userInfos;
    }

    public static User userInfoParseUser(UserInfo userInfo) {
        User user = new User();
        user.setUserId(userInfo.getUserId());
        if (UserRole.ADMIN.getRole().equals(userInfo.getRole())) {
            user.setRoleId(UserRole.ADMIN.getCode());
        }
        if (UserRole.VIP.getRole().equals(userInfo.getRole())) {
            user.setRoleId(UserRole.VIP.getCode());
        }
        if (UserRole.COMMON.getRole().equals(userInfo.getRole())) {
            user.setRoleId(UserRole.COMMON.getCode());
        }
        user.setUserName(userInfo.getUserName());
        user.setNickName(userInfo.getNickName());
        user.setEmail(userInfo.getEmail());
        user.setPhoneNumber(userInfo.getPhoneNumber());
        if (UserSex.MALE.getSex().equals(userInfo.getSex())) {
            user.setSex(UserSex.MALE.getCode());
        }
        if (UserSex.WOMAN.getSex().equals(userInfo.getSex())) {
            user.setSex(UserSex.WOMAN.getCode());
        }
        user.setAvatar(userInfo.getAvatar());
        user.setPassword(userInfo.getPassword());
        if (UserStatus.NORMAL.getStatus().equals(userInfo.getStatus())) {
            user.setStatus(UserStatus.NORMAL.getCode());
        }
        if (UserStatus.DISABLE.getStatus().equals(userInfo.getStatus())) {
            user.setStatus(UserStatus.DISABLE.getCode());
        }
        if (UserStatus.DELETE.getStatus().equals(userInfo.getStatus())) {
            user.setStatus(UserStatus.DELETE.getCode());
        }
        user.setLoginIp(userInfo.getLoginIp());
        user.setLoginDate(DateUtils.TimeParseDate(userInfo.getLoginDate()));
        user.setCreateTime(DateUtils.TimeParseDate(userInfo.getCreateTime()));
        user.setCreateBy(userInfo.getCreateBy());
        user.setUpdateBy(userInfo.updateBy);
        user.setUpdateTime(DateUtils.TimeParseDate(userInfo.getUpdateTime()));
        user.setRemark(userInfo.getRemark());

        return user;
    }
}
