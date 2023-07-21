package com.opentool.dashboard.domain.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opentool.dashboard.domain.entity.User;
import com.opentool.dashboard.domain.vo.UserInfo;
import lombok.Data;

import java.util.ArrayList;
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

    public PageUser(Long total, List<User> users) {
        this.total = total;
        this.userInfos = UserInfo.userConvertUserInfoList(users);
    }
}
