package com.opentool.dashboard.domain.vo;

import lombok.Data;


/**
 * 数据分析控制器
 *
 * @author ZenSheep
 * @date 2023/07/09
 */
@Data
public class OperationData {
    private Long accessNum;
    private Long userNum;
    private Long collectNum;
    private Long contributionNum;
}
