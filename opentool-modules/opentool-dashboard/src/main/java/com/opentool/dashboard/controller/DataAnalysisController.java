package com.opentool.dashboard.controller;

import com.opentool.common.core.domain.R;
import com.opentool.dashboard.domain.entify.LoginLog;
import com.opentool.dashboard.domain.vo.OperationData;
import com.opentool.dashboard.service.IDataAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 数据分析控制器
 *
 * @author ZenSheep
 * @date 2023/07/09
 */
@RefreshScope
@RestController
@RequestMapping("/data-analysis")
public class DataAnalysisController {
    @Autowired
    private IDataAnalysisService dataAnalysisService;

    /**
     * 网站运营相关数据
     */
    @GetMapping("/operation")
    public R<?> getOperationData(){
        OperationData operationData = new OperationData();

        operationData.setAccessNum((long) dataAnalysisService.getLoginLogList().size());
        operationData.setUserNum((long) dataAnalysisService.getUserList().size());
        operationData.setCollectNum((long) dataAnalysisService.getCollectLogList().size());
        operationData.setContributionNum((long) dataAnalysisService.getContributeLogList().size());

        return R.ok(operationData);
    }

    /**
     * 根据日期范围获取用户访问数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return userData、accessData、contributeData、collectData: Map<String, List<Long>>
     */
    @GetMapping("/date/{startDate}/{endDate}")
    public R<?> getAccessDateByDateRange(@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate){
        // 获得endDate后一天
        LocalDate endLocalDate = LocalDate.parse(endDate);
        LocalDate nextDay = endLocalDate.plusDays(1);
        String nextDayStr = nextDay.format(DateTimeFormatter.ISO_DATE);

        return R.ok(dataAnalysisService.getDataByDateRange(startDate, nextDayStr));
    }

    /**
     * 新增登录日志
     * 请求体：除了id以外的所有字段
     */
    @PostMapping("/loginlog")
    public R<?> addOperationData(@RequestBody LoginLog loginLog){
        dataAnalysisService.insertLoginLog(loginLog);
        return R.ok();
    }
}
