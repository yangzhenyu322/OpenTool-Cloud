package com.opentool.system.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * OSS服务接口
 * / @Author: ZenSheep
 * / @Date: 2023/8/10 16:04
 */
public interface IOSSService {
    String uploadFile(MultipartFile file, String storagePath);
}
