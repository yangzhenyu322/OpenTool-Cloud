package com.opentool.dashboard.controller;

import com.opentool.dashboard.service.IWorkBenchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/15 16:32
 */
@RestController
@RequestMapping("/dashboard/workbench")
public class WorkBenchController {
    @Autowired
    private IWorkBenchService workBenchService;


}
