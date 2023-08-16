package com.opentool.general.tool.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 图像转换服务接口
 * / @Author: ZenSheep
 * / @Date: 2023/8/14 18:52
 */
public interface IImageConvertService {
    String uploadFile(MultipartFile file, String storagePath);
}
