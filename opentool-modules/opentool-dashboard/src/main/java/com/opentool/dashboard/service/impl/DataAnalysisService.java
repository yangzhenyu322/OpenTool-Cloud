package com.opentool.dashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.opentool.dashboard.domain.entify.CollectLog;
import com.opentool.dashboard.domain.entify.ContributeLog;
import com.opentool.dashboard.domain.entify.LoginLog;
import com.opentool.dashboard.mapper.CollectMapper;
import com.opentool.dashboard.mapper.ContributeLogMapper;
import com.opentool.dashboard.mapper.LoginLogMapper;
import com.opentool.dashboard.service.IDataAnalysisService;
import com.opentool.system.api.RemoteUserService;
import com.opentool.system.api.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/11 17:59
 */
@Service
public class DataAnalysisService implements IDataAnalysisService {
    @Autowired
    private LoginLogMapper loginLogMapper;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private CollectMapper collectMapper;

    @Autowired
    private ContributeLogMapper contributeLogMapper;

    @Override
    public List<LoginLog> getLoginLogList() {
        return loginLogMapper.selectList(null);
    }

    @Override
    public boolean insertLoginLog(LoginLog loginLog) {
        return loginLogMapper.insert(loginLog) > 0;
    }

    @Override
    public List<SysUser> getUserList() {
        return remoteUserService.getUserList();
    }

    @Override
    public List<CollectLog> getCollectLogList() {
        return collectMapper.selectList(null);
    }

    @Override
    public List<ContributeLog> getContributeLogList() {
        return contributeLogMapper.selectList(null);
    }

    /**
     * 通过日期获得特定周的访客数据: [startDate,endDate)
     */
    @Override
    public Map<String, List<Long>> getDataByDateRange(String startDate, String endDate) {
        Map<String, List<Long>> resultMaps = new HashMap<>();
        // accessData
        QueryWrapper<LoginLog> accessQuery = new QueryWrapper<>();
        accessQuery.select("DATE(visits_time) AS visits_time", "COUNT(*) AS count")
                .between("visits_time", startDate, endDate)
                .groupBy("DATE(visits_time)")
                .orderByAsc("visits_time");
        List<Map<String, Object>> result = loginLogMapper.selectMaps(accessQuery);
        resultMaps.put("accessData",countDataByResult(result, "visits_time", startDate, endDate));

        // userData
        result = remoteUserService.getUserDataByDate(startDate, endDate);
        resultMaps.put("userData",countDataByResult(result, "create_time", startDate, endDate));

        // collectData
        QueryWrapper<CollectLog> collectQuery = new QueryWrapper<>();
        collectQuery.select("DATE(collect_time) AS collect_time", "COUNT(*) AS count")
                .between("collect_time", startDate, endDate)
                .groupBy("DATE(collect_time)")
                .orderByAsc("collect_time");
        result = collectMapper.selectMaps(collectQuery);
        resultMaps.put("collectData",countDataByResult(result, "collect_time", startDate, endDate));

        // contributeDate
        QueryWrapper<ContributeLog> contributeQuery = new QueryWrapper<>();
        contributeQuery.select("DATE(contribute_time) AS contribute_time", "COUNT(*) AS count")
                .between("contribute_time", startDate, endDate)
                .groupBy("DATE(contribute_time)")
                .orderByAsc("contribute_time");
        result = contributeLogMapper.selectMaps(contributeQuery);
        resultMaps.put("contributeData", countDataByResult(result, "contribute_time", startDate, endDate));

        return resultMaps;
    }

    // 通过查询语句获得范围日期内每一天的统计数据
    public List<Long> countDataByResult(List<Map<String, Object>> result, String timeStr , String startDate, String endDate) {
        List<Long> countResults = new ArrayList<>();
        // 日期字符串转LocalDate类型方便计算两天之间的间隔天数
        LocalDate startLocalDate = LocalDate.parse(startDate);
        LocalDate endLocalDate = LocalDate.parse(endDate);

        if(result.size() <= 0) {
            long days = ChronoUnit.DAYS.between(startLocalDate, endLocalDate);
            for(int i = 0; i < days; i++) {
                countResults.add(0L);
            }
            return countResults;
        }

        // 前面补零
        LocalDate headDate = LocalDate.parse(result.get(0).get(timeStr).toString());
        long headZeros = ChronoUnit.DAYS.between(startLocalDate, headDate);
        for(int i = 0; i < headZeros; i++){
            countResults.add(0L);
        }

        for(Map<String, Object> row: result) {
            // System.out.println(row);
            countResults.add(Long.parseLong(row.get("count").toString()));
        }

        // 后面补零
        LocalDate tailDate = LocalDate.parse(result.get(result.size() - 1).get(timeStr).toString());
        long tailZeros = ChronoUnit.DAYS.between(tailDate, endLocalDate) - 1;
        for(int i = 0; i < tailZeros; i++){
            countResults.add(0L);
        }

        return countResults;
    }

}
