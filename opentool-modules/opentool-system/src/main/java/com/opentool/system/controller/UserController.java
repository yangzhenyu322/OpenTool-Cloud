package com.opentool.system.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opentool.common.core.domain.R;
import com.opentool.system.api.domain.SysUser;
import com.opentool.system.domain.vo.PageUser;
import com.opentool.system.domain.dto.UserInfo;
import com.opentool.system.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/13 17:40
 */

@RefreshScope
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;

    /**
     * IP定位API（腾讯位置服务，第三方）：https://lbs.qq.com/service/webService/webServiceGuide/webServiceIp
     * @param userIP
     * @return
     */
    @GetMapping("/location/{ip}")
    public JSONObject getUserIp(@PathVariable("ip") String userIP) {
        String key = "GAHBZ-56GKQ-Y6I5F-4IH67-4424Z-NSFFD";
        String url = "https://apis.map.qq.com/ws/location/v1/ip?ip=" + userIP + "&key=" + key;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        JSONObject jsonObject = JSON.parseObject(response.getBody());

        return jsonObject;
    }

    /**
     * 获取天气API（高德开放平台，第三方）：https://lbs.amap.com/api/webservice/guide/api/weatherinfo/#t1
     * @param city
     * @return
     */
    @GetMapping("/weather/{city}")
    public JSONObject getCityWeather(@PathVariable("city") String city) {
        String key = "e186a4764789b2381204f4327f27326b";
        String url = "https://restapi.amap.com/v3/weather/weatherInfo?key=" + key +  "&city=" + city;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String>  response = restTemplate.getForEntity(url, String.class);
        JSONObject jsonObject = JSON.parseObject(response.getBody());

        return jsonObject;
    }

    /**
     * 分页查询User
     * @param page
     * @param size
     * total: 总条数（不是size，是所有User的length）
     * @return
     */
    @GetMapping("/{page}/{size}")
    public PageUser getPageUserList(
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size
    ) {
        Page<SysUser> result = userService.page(new Page<>(page, size));
        return new PageUser(result.getTotal(), result.getRecords());
    }

    /**
     * 更新用户
     * @param userInfo
     * @return
     */
    @PutMapping("/userInfo")
    public R<?> updateUser(@RequestBody UserInfo userInfo) {
        boolean isUpdated = userService.updateById(UserInfo.userInfoParseUser(userInfo));

        if(isUpdated) {
            System.out.println("更新用户信息:" + userInfo);
        }else {
            System.out.println("用户信息更新失败");
        }

        return isUpdated ? R.ok(null,"用户信息更新成功") : R.fail("用户信息更新失败");
    }

    /**
     * 删除用户
     * @param userId
     * @return
     */
    @DeleteMapping("/{id}")
    public R<?> deleteUser(@PathVariable("id") String userId) {
        boolean isDeleted =  userService.removeById(userId);

        if(isDeleted) {
            System.out.println("删除用户信息：UID-" + userId);
        }else {
            System.out.println("用户信息删除失败");
        }

        return isDeleted ? R.ok(null, "用户信息删除成功") : R.fail("用户信息删除失败");
    }


    /**
     * 获取所有用户
     * @return
     */
    @GetMapping("/lists")
    public List<SysUser> getUserList() {
        return userService.queryUserList();
    }

    /**
     * 通过用户名获取用户信息
     * @param username
     * @return
     */
    @GetMapping("/username/{username}")
    public SysUser findUserByUserName(@PathVariable("username") String username) {
        return userService.findUserByUserName(username);
    }

    /**
     * 检查手机号码是否被注册过
     * @param phoneNumber
     * @return 没有返回OK，有则返回FAIL
     */
    @GetMapping("/check/phone/{phoneNumber}")
    public R<?> checkPhoneNumber(@PathVariable("phoneNumber") String phoneNumber) {
        return userService.checkPhoneNumberRegistered(phoneNumber);
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @PostMapping("/register/{codeRequestId}/{inputCode}")
    public R<?> register(@RequestBody SysUser user,
                         @PathVariable("codeRequestId") String codeRequestId,
                         @PathVariable("inputCode") String inputCode) {
        return userService.registerUser(user, codeRequestId, inputCode);
    }

    /**
     * 检测用户名与手机号码是否一致
     * @param username
     * @param phoneNumber
     * @return
     */
    @GetMapping("/check/username/{username}/phone/{phoneNumber}")
    public R<?> checkUsernameAndPhoneConsistent(@PathVariable("username") String username,
                                                @PathVariable("phoneNumber") String phoneNumber) {
        return userService.checkUsernameAndPhoneConsistent(username, phoneNumber);
    }

    @PutMapping("/password")
    public R<?> updateUserPassword(@RequestBody UserInfo userInfo) {
        return userService.updateUserPassword(userInfo);
    }

    /**
     * 获取用户maps
     * @param
     * @return
     */
    @GetMapping("/maps/{startDate}/{endDate}")
    public List<Map<String, Object>> getUserDataByDate(@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) {
        return userService.getSelectMaps(startDate, endDate);
    }
}
