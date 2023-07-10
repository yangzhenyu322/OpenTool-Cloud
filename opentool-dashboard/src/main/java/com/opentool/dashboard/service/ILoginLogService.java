package com.opentool.dashboard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.opentool.dashboard.domain.entity.LoginLog;

import java.util.List;


/**
 * login日志服务接口
 *
 * @author ZenSheep
 * @date 2023/07/09
 */
public interface ILoginLogService extends IService<LoginLog> {
    List<LoginLog> getLoginLogList();
}
