package com.opentool.system.controller;

import com.opentool.system.service.IOSSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * OSS 文件管理接口类
 * / @Author: ZenSheep
 * / @Date: 2023/8/13 22:07
 */
@RefreshScope
@RestController
@RequestMapping("/oss/file")
public class OSSFileController {
    @Autowired
    private IOSSService ossService;

    /**
     * 文件上传，入参可以根据具体业务进行添加
     * @param file 文件
     * @return 响应结果
     */
    @PostMapping( value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFile(@RequestPart("file") MultipartFile file, @RequestParam("storagePath") String storagePath) {
        return ossService.uploadFile(file, storagePath);
    }


}