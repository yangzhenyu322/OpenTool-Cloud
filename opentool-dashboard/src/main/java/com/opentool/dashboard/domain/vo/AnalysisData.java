package com.opentool.dashboard.domain.vo;

import lombok.Data;

/**
 *
 * / @Author: ZenSheep
 * / @Date: 2023/7/12 21:14
 */
@Data
public class AnalysisData {
    private Long accessCount;
    private Long userCount;
    private Long collectCount;
    private Long contributeCount;
}
