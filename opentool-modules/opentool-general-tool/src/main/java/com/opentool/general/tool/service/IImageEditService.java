package com.opentool.general.tool.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 图像编辑服务接口
 * / @Author: ZenSheep
 * / @Date: 2023/8/23 17:40
 */
public interface IImageEditService {
    String uploadFile(MultipartFile file);
}
