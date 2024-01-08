package com.opentool.system.domain.vo;

import com.opentool.system.api.domain.SysUser;
import com.opentool.system.domain.dto.UserInfo;
import lombok.Data;

import java.util.List;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/19 20:41
 */
@Data
public class PageUser {
    private Long total;
    private List<UserInfo> userInfos;

    public PageUser() {

    }

    public PageUser(Long total, List<SysUser> sysUsers) {
        this.total = total;
        this.userInfos = UserInfo.userConvertUserInfoList(sysUsers);
    }
}
