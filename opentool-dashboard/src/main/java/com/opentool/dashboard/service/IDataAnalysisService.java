package com.opentool.dashboard.service;

import com.opentool.dashboard.domain.entity.CollectLog;
import com.opentool.dashboard.domain.entity.ContributeLog;
import com.opentool.dashboard.domain.entity.LoginLog;
import com.opentool.dashboard.domain.entity.User;

import java.util.List;
import java.util.Map;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/11 17:59
 */
public interface IDataAnalysisService {

    List<LoginLog> getLoginLogList();

    boolean insertLoginLog(LoginLog loginLog);

    List<User> getUserList();

    List<CollectLog> getCollectLogList();

    List<ContributeLog> getContributeLogList();

    Map<String, List<Long>> getDataByDateRange(String startDate, String endDate);
}
