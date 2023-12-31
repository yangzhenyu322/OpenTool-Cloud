package com.opentool.general.tool.service.impl;

import com.opentool.common.core.utils.file.FileUtils;
import com.opentool.common.core.utils.file.MultipartFileUtils;
import com.opentool.general.tool.domain.vo.ConvertConfigInfo;
import com.opentool.general.tool.service.IImageConvertService;
import com.opentool.general.tool.utils.file.ImageConvertUtils;
import com.opentool.system.api.RemoteFileService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
public class ImageConvertService implements IImageConvertService {
    @Autowired
    private RemoteFileService remoteFileService;

    @Override
    public String uploadFile(MultipartFile file, String storagePath) {
       return remoteFileService.uploadFile(file, storagePath);
    }

    @Override
    public List<String> urlsFormatConvert(List<String> urlsStrList, String targetFormat, ConvertConfigInfo convertConfigInfo) throws IOException {
        List<String> urlsTargetPath = new ArrayList<>();
        List<File> fileList = ImageConvertUtils.urlsFormatConvert(urlsStrList, targetFormat, convertConfigInfo);

        for (File file:fileList) {
            MultipartFile multipartFile = MultipartFileUtils.fileToMultipartFile(file);
            urlsTargetPath.add(remoteFileService.uploadFile(multipartFile, "ImageConvert/images/convert"));
            // 删除缓存本地文件
            FileUtils.deleteFile(file);
        }

        return urlsTargetPath;
    }

    @Override
    public List<String> urlsSizeConvert(List<String> urlsStrList, int width, int height) throws IOException {
        List<String> urlsTargetPath = new ArrayList<>();
        List<File> fileList = ImageConvertUtils.urlsSizeConvert(urlsStrList, width, height);

        for (File file:fileList) {
            MultipartFile multipartFile = MultipartFileUtils.fileToMultipartFile(file);
            urlsTargetPath.add(remoteFileService.uploadFile(multipartFile, "ImageConvert/images/convert"));
            // 删除缓存本地文件
            FileUtils.deleteFile(file);
        }

        return urlsTargetPath;
    }
}