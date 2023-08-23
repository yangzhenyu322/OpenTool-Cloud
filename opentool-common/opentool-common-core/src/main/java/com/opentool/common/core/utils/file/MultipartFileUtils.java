package com.opentool.common.core.utils.file;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * MultipartFile 工具类
 * / @Author: ZenSheep
 * / @Date: 2023/8/19 18:59
 */
public class MultipartFileUtils {
    /**
     * MultipartFile 转 File
     * @param multipartFile
     * @return
     * @throws IOException
     */
    public static File toFile(MultipartFile multipartFile) throws IOException {
        File file = File.createTempFile("temp", null); // 创建临时文件
        multipartFile.transferTo(file); // 将 MultipartFile 写入临时文件

        return file;
    }

    /**
     * File 转 MultipartFile
     * @param file
     * @return
     * @throws IOException
     */
    public static MultipartFile fileToMultipartFile(File file) throws IOException {
        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), null, new FileInputStream(file));
        return multipartFile;
    }
}
