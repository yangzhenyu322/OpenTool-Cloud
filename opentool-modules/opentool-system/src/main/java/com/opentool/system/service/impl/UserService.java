package com.opentool.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opentool.system.api.domain.SysUser;
import com.opentool.system.mapper.UserMapper;
import com.opentool.system.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/13 17:40
 */
@Service
public class UserService extends ServiceImpl<UserMapper, SysUser> implements IUserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public List<SysUser> queryUserList() {
        return userMapper.selectList(null);
    }

    @Override
    public List<Map<String, Object>> getSelectMaps(String startDate, String endDate) {
        QueryWrapper<SysUser> userQuery = new QueryWrapper<>();
        userQuery.select("DATE(create_time) AS create_time", "COUNT(*) AS count")
                .between("create_time", startDate, endDate)
                .groupBy("DATE(create_time)")
                .orderByAsc("create_time");
        return userMapper.selectMaps(userQuery);
    }

    @Override
    public SysUser findUserByUserName(String username) {
        Map<String, Object> map = new HashMap<>();
        map.put("user_name", username);
        List<SysUser> sysUsers = userMapper.selectByMap(map);
        SysUser user = null;
        if (sysUsers.size() > 0) {
            user = sysUsers.get(0);
        }

        return user;
    }
}
