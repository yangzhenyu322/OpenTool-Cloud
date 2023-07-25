package com.opentool.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.opentool.system.api.domain.User;

import java.util.List;
import java.util.Map;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/13 17:39
 */
public interface IUserService extends IService<User> {
    List<User> queryUserList();

    List<Map<String, Object>> getSelectMaps(String startDate, String endDate);
}
