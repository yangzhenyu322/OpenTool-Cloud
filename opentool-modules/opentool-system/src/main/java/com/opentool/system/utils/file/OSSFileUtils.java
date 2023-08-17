package com.opentool.system.utils.file;

import org.apache.commons.io.FilenameUtils;

/**
 * OSS 文件工具类
 * / @Author: ZenSheep
 * / @Date: 2023/8/14 9:28
 */
public class OSSFileUtils {
    /**
     * 获取文件名（不包含后缀）
     * @param fileName 元文件名（包含后缀）
     * @return
     */
    public static String getBaseName(String fileName) {
        return FilenameUtils.getBaseName(fileName);
    }

    /**
     * 获取文件类型
     * @param fileName 文件名（包含后缀）
     * @return 文件类型，如 image/bmp
     */
    public static String getcontentType(String fileName) {
        fileName = fileName.substring(fileName.lastIndexOf("."));

        if (fileName.equalsIgnoreCase(".bmp")) {
            return "image/bmp";
        }
        if (fileName.equalsIgnoreCase(".gif")) {
            return "image/gif";
        }
        if (fileName.equalsIgnoreCase(".jpeg") ||
                fileName.equalsIgnoreCase(".jpg") ||
                fileName.equalsIgnoreCase(".png")) {
            return "image/jpg";
        }
        if (fileName.equalsIgnoreCase(".html")) {
            return "text/html";
        }
        if (fileName.equalsIgnoreCase(".txt")) {
            return "text/plain";
        }
        if (fileName.equalsIgnoreCase(".vsd")) {
            return "application/vnd.visio";
        }
        if (fileName.equalsIgnoreCase(".pptx") ||
                fileName.equalsIgnoreCase(".ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (fileName.equalsIgnoreCase(".docx") ||
                fileName.equalsIgnoreCase(".doc")) {
            return "application/msword";
        }
        if (fileName.equalsIgnoreCase(".xml")) {
            return "text/xml";
        }
        return "image/jpg";
    }
}