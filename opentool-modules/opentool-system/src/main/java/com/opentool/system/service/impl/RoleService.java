package com.opentool.system.service.impl;

import com.opentool.system.api.domain.SysRole;
import com.opentool.system.mapper.RoleMapper;
import com.opentool.system.service.IRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * / @Author: ZenSheep
 * / @Date: 2024/1/5 22:54
 */
@Slf4j
@Service
public class RoleService implements IRoleService {
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public SysRole getSysRoleById(Long id) {
        return roleMapper.selectById(id);
    }
}
