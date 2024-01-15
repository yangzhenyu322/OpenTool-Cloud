package com.opentool.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opentool.common.core.domain.R;
import com.opentool.system.api.domain.SysUser;
import com.opentool.system.mapper.UserMapper;
import com.opentool.system.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/13 17:40
 */
@Slf4j
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

    @Override
    public R<?> registerUser(SysUser user) {
        log.info("Begin Register: " + user.getUserName());

        // 检测用户名是否为空
        if (!StringUtils.hasText(user.getUserName())) {
            return R.fail("用户名不能为空");
        }

        // 检测用户名是否重复
        Map<String, Object> userNameMap = new HashMap<>();
        userNameMap.put("user_name", user.getUserName());
        boolean isUserNameExit = userMapper.selectByMap(userNameMap).size() > 0;

        if (isUserNameExit) {
            return R.fail("该用户名已被注册");
        }

        // 密码格式检验
        if (!StringUtils.hasText(user.getPassword())) {
            // 密码为空
            return R.fail("密码不能为空");
        }

        // 检测手机号格式是否正确
        if (user.getPhoneNumber().length() == 0) {
            return R.fail("手机号不能为空");
        }

        if (user.getPhoneNumber().length() != 11) {
            return R.fail("手机号长度不正确");
        }

        if (!isStr2Num(user.getPhoneNumber())) {
            return R.fail("手机号格式不正确");
        }

        // 检测手机号是否被注册过
        Map<String, Object> phoneMap = new HashMap<>();
        phoneMap.put("phone_number", user.getPhoneNumber());
        boolean isPhoneNumberExit = userMapper.selectByMap(phoneMap).size() > 0;

        if (isPhoneNumberExit) {
            return R.fail("该手机号已注册账户");
        }

        // 插入新用户
        user.setRoleId(3L);  // 设置默认权限
        boolean isInsertSuccess = userMapper.insert(user) > 0;  // 返回值int代表插入成功条数，大于0表示插入成功条数，等于0则代表插入失败

        if (!isInsertSuccess) {
            log.info("用户[{}]注册失败：", user.getUserName());
            return R.fail("用户注册失败");
        }

        log.info("用户[{}]注册成功：", user.getUserName());
        return R.ok(null, "用户注册成功");
    }

    public static boolean isStr2Num(String str) {
        Pattern pattern = Pattern.compile("^[0-9]*$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}
