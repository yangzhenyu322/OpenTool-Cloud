package com.opentool.general.tool.service.impl;

import com.opentool.general.tool.service.IImageConvertService;
import com.opentool.system.api.RemoteFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图像转换服务类
 * / @Author: ZenSheep
 * / @Date: 2023/8/14 18:53
 */
@Service
public class ImageConvertService implements IImageConvertService {
    @Autowired
    private RemoteFileService remoteFileService;

    @Override
    public String uploadFile(MultipartFile file, String storagePath) {
       return remoteFileService.uploadFile(file, storagePath);
    }
}
