package com.opentool.general.tool.controller;

import com.opentool.common.core.domain.R;
import com.opentool.general.tool.service.IImageEditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图像编辑控制类
 * / @Author: ZenSheep
 * / @Date: 2023/8/23 17:36
 */
@RefreshScope
@RestController
@RequestMapping("/imageEdit")
public class ImageEditController {
    @Autowired
    IImageEditService imageEditService;

    @PostMapping("/upload")
    public R<?> uploadImage(@RequestPart("file")MultipartFile file) {
        return R.ok(imageEditService.uploadFile(file, "imageEdit/images/origin"));
    }
}