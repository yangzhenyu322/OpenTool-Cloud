package com.opentool.dashboard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opentool.dashboard.domain.entity.User;
import com.opentool.dashboard.mapper.UserMapper;
import com.opentool.dashboard.service.IUserService;
import org.springframework.stereotype.Service;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/13 17:40
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> implements IUserService {
}
