package com.opentool.dashboard.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.opentool.dashboard.common.domain.R;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.net.InetAddress;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/13 17:40
 */

@RefreshScope
@RestController
@RequestMapping("/dashboard/users")
public class UserController {
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
}
