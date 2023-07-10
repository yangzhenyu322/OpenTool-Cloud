package com.opentool.dashboard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opentool.dashboard.domain.entity.User;
import com.opentool.dashboard.mapper.UserMapper;
import com.opentool.dashboard.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/10 18:26
 */
@Service
public class UsrService extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public List<User> getUserList() {
        return userMapper.selectList(null);
    }
}
