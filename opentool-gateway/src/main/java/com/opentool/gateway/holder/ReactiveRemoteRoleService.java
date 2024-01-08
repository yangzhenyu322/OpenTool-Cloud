package com.opentool.gateway.holder;

import com.opentool.system.api.RemoteRoleService;
import com.opentool.system.api.domain.SysRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

/**
 * / @Author: ZenSheep
 * / @Date: 2024/1/7 12:19
 */
@Slf4j
@Component
public class ReactiveRemoteRoleService {
    @Lazy
    @Autowired
    private RemoteRoleService remoteRoleService;

    @Async
    public Future<SysRole> getSysRoleById(Long id) {
        SysRole role = remoteRoleService.getSysRoleById(id);

        return new AsyncResult<>(role);
    }
}
