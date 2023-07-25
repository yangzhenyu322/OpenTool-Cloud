package com.opentool.system.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.opentool.system.api.domain.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * User Feign：提供User的远程服务
 * / @Author: ZenSheep
 * / @Date: 2023/7/25 10:26
 */
@FeignClient(name = "opentool-system")
public interface RemoteUserService {
    /**
     * 查询用户列表
     * @return
     */
    @GetMapping("/user/lists")
    List<User> getUserList();

    /**
     * 查询用户数据
     * @param startDate
     * @param endDate
     * @return
     */
    @GetMapping("/user/maps/{startDate}/{endDate}")
    public List<Map<String, Object>> getUserDataByDate(@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate);
}
