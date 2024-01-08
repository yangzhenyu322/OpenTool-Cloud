package com.opentool.dashboard.service;

import com.opentool.dashboard.domain.entify.CollectLog;
import com.opentool.dashboard.domain.entify.ContributeLog;
import com.opentool.dashboard.domain.entify.LoginLog;
import com.opentool.system.api.domain.SysUser;

import java.util.List;
import java.util.Map;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/11 17:59
 */
public interface IDataAnalysisService {

    List<LoginLog> getLoginLogList();

    boolean insertLoginLog(LoginLog loginLog);

    List<SysUser> getUserList();

    List<CollectLog> getCollectLogList();

    List<ContributeLog> getContributeLogList();

    Map<String, List<Long>> getDataByDateRange(String startDate, String endDate);
}
