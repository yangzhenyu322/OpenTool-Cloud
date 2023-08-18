package com.opentool.general.tool.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 图像转换服务接口
 * / @Author: ZenSheep
 * / @Date: 2023/8/14 18:52
 */
public interface IImageConvertService {
    String uploadFile(MultipartFile file, String storagePath);

    List<String> urlsFormatConvert(List<String> urlsStrList, String targetFormat, String storagePath) throws IOException;
}
