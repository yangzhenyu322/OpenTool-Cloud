package com.opentool.dashboard.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opentool.dashboard.common.domain.R;
import com.opentool.dashboard.domain.dto.PageUser;
import com.opentool.dashboard.domain.entity.User;
import com.opentool.dashboard.domain.vo.UserInfo;
import com.opentool.dashboard.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/13 17:40
 */

@RefreshScope
@RestController
@RequestMapping("/dashboard/users")
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
        Page<User> result = userService.page(new Page<>(page, size));
        return new PageUser(result.getTotal(), result.getRecords());
    }

    @PutMapping("/")
    public R<?> updateUser(@RequestBody UserInfo userInfo) {
        boolean isUpdated = userService.updateById(UserInfo.userInfoParseUser(userInfo));

        if(isUpdated) {
            System.out.println("更新用户信息:" + userInfo);
        }else {
            System.out.println("用户信息更新失败");
        }

        return isUpdated ? R.ok(null,"用户信息更新成功") : R.fail("用户信息更新失败");
    }

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
}
