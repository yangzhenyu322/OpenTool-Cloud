package com.opentool.system.api;

import com.opentool.system.api.config.DefaultFeignConfiguration;
import com.opentool.system.api.domain.SysRole;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Role Feign：提供Role的远程服务
 * @Author: ZenSheep
 * @Date: 2024/1/5 22:59
 */
@FeignClient(name = "opentool-system", contextId = "remote-role", configuration = DefaultFeignConfiguration.class)
public interface RemoteRoleService {

    @GetMapping("/role/id/{id}")
    SysRole getSysRoleById(@PathVariable("id") Long id);
}
