package com.opentool.general.tool.service;

import com.opentool.general.tool.domain.vo.ConvertConfigInfo;
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

    List<String> urlsFormatConvert(List<String> urlsStrList, String targetFormat, ConvertConfigInfo convertConfigInfo, String storagePath) throws IOException;

    List<String> urlsSizeConvert(List<String> urlsStrList, int width, int height) throws IOException;
}
