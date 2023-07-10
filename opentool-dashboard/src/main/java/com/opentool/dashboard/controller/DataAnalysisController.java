package com.opentool.dashboard.controller;

import com.opentool.dashboard.common.constant.DataConstants;
import com.opentool.dashboard.common.domain.R;
import com.opentool.dashboard.domain.entity.LoginLog;
import com.opentool.dashboard.domain.vo.OperationData;
import com.opentool.dashboard.service.ILoginLogService;
import com.opentool.dashboard.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 数据分析控制器
 *
 * @author ZenSheep
 * @date 2023/07/09
 */
@RestController
@RequestMapping("/data-analysis")
@RefreshScope
public class DataAnalysisController {
    @Autowired
    private ILoginLogService loginLogService;

    @Autowired
    private IUserService userService;

    /**
     * 获取经营相关数据
     */
    @GetMapping("/data")
    @CrossOrigin()
    public R<?> getOperationData(){
        OperationData operationData = new OperationData();

        operationData.setAccessNum((long) loginLogService.getLoginLogList().size());
        operationData.setUserNum((long) userService.getUserList().size());
        operationData.setCollectNum(DataConstants.COLLECT_NUM);
        operationData.setContributionNum(DataConstants.CONTRIBUTION_NUM);

        return R.ok(operationData);
    }

    /**
     * 新增登录日志
     * 请求体：除了id以外的所有字段
     */
    @PostMapping("/loginlog")
    public R<?> addOperationData(@RequestBody LoginLog loginLog){
        loginLogService.save(loginLog);
        return R.ok();
    }
}
