package com.opentool.general.tool.service.impl;

import com.opentool.general.tool.service.IImageEditService;
import com.opentool.system.api.RemoteFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图像编辑服务实现类
 * / @Author: ZenSheep
 * / @Date: 2023/8/23 17:40
 */
@Service
public class ImageEditService implements IImageEditService {
    @Autowired
    private RemoteFileService remoteFileService;

    @Override
    public String uploadFile(MultipartFile file) {
        return remoteFileService.uploadFile(file, "ImageEdit/images/origin");
    }
}
