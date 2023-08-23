package com.opentool.general.tool.service.impl;

import com.opentool.common.core.utils.file.MultipartFileUtils;
import com.opentool.general.tool.service.IImageConvertService;
import com.opentool.general.tool.utils.file.ImageConvertUtils;
import com.opentool.system.api.RemoteFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 图像转换服务实现类
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

    @Override
    public List<String> urlsFormatConvert(List<String> urlsStrList, String targetFormat, String storagePath) throws IOException {
        List<String> urlsTargetPath = new ArrayList<>();
        List<File> fileList = ImageConvertUtils.urlsFormatConvert(urlsStrList, targetFormat);

        for (File file:fileList) {
            MultipartFile multipartFile = MultipartFileUtils.fileToMultipartFile(file);
            urlsTargetPath.add(remoteFileService.uploadFile(multipartFile, storagePath));
        }

        return urlsTargetPath;
    }
}