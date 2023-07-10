package com.opentool.dashboard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.opentool.dashboard.domain.entity.User;

import java.util.List;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/10 18:25
 */
public interface IUserService extends IService<User> {
    List<User> getUserList();
}
