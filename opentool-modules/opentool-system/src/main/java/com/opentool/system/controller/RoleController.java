package com.opentool.system.controller;

import com.opentool.system.api.domain.SysRole;
import com.opentool.system.service.IRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * / @Author: ZenSheep
 * / @Date: 2024/1/5 22:55
 */
@Slf4j
@RefreshScope
@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private IRoleService roleService;

    @GetMapping("/id/{id}")
    public SysRole getSysRoleById(@PathVariable("id") Long id) {
        return roleService.getSysRoleById(id);
    }
}
