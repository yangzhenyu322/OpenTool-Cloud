package com.opentool.general.tool.controller;

import com.alibaba.fastjson2.JSON;
import com.opentool.common.core.domain.R;
import com.opentool.general.tool.domain.vo.ConvertConfigInfo;
import com.opentool.general.tool.service.IImageConvertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 图像转换控制类
 * / @Author: ZenSheep
 * / @Date: 2023/8/14 18:59
 */
@RefreshScope
@RestController
@RequestMapping("/imageConvert")
public class ImageConvertController {
    @Autowired
    IImageConvertService imageConvertService;
    @PostMapping("/upload")
    public R<?> uploadFile(@RequestPart("file") MultipartFile file) {
        return R.ok(imageConvertService.uploadFile(file, "ImageConvert/images/origin"));
    }

    /**
     * 根据指定类型和格式转换图片
     * @param urlsStrList
     * @param targetFormat
     * @param convertConfig
     * @return
     * @throws IOException
     */
    @PostMapping("/conversion")
    public R<?> convertFormat(
            @RequestParam("urlsStrList") List<String> urlsStrList,
            @RequestParam("targetFormat") String targetFormat,
            @RequestParam("convertConfig") String convertConfig) throws IOException {
        // 将JSON对象反序列为ConvertConfigInfo对象
        ConvertConfigInfo convertConfigInfo = JSON.parseObject(convertConfig, ConvertConfigInfo.class);
        return R.ok(imageConvertService.urlsFormatConvert(urlsStrList, targetFormat, convertConfigInfo,"ImageConvert/images/convert"));
    }

    /**
     * 根据指定长宽转换图片
     * @param urlsStrList
     * @param width
     * @param height
     * @return
     * @throws IOException
     */
    @PostMapping("/conversion/size")
    public List<String> convertSize(@RequestParam("urlsStrList") List<String> urlsStrList,
                            @RequestParam("width") int width,
                            @RequestParam("height") int height) throws IOException {
        return imageConvertService.urlsSizeConvert(urlsStrList, width, height);
    }
}