package com.opentool.general.tool.controller;

import com.opentool.common.core.domain.R;
import com.opentool.general.tool.service.IImageConvertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图像转换控制类
 * / @Author: ZenSheep
 * / @Date: 2023/8/14 18:59
 */
@RefreshScope
@RestController
@RequestMapping("/imageConvert")
public class ImageConvertController {
    @Autowired
    IImageConvertService iImageConvertService;
    @PostMapping("/upload")
    public R<?> uploadFile(@RequestPart("file") MultipartFile file) {
        return R.ok(iImageConvertService.uploadFile(file, "ImageConvert/images"));
    }
}