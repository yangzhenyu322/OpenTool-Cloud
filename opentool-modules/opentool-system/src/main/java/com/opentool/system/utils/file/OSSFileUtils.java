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

        if (fileName.equalsIgnoreCase(".jpg")) {
            return "image/jpeg";
        }
        if (fileName.equalsIgnoreCase(".jpeg")) {
            return "image/jpeg";  // 支持预览（其它格式在OSS不支持预览）
        }
        if (fileName.equalsIgnoreCase(".png")) {
            return "image/png";
        }
        if (fileName.equalsIgnoreCase(".gif")) {
            return "image/gif";
        }
        if (fileName.equalsIgnoreCase(".tif")) {
            return "image/tif";
        }
        if (fileName.equalsIgnoreCase(".tiff")) {
            return "image/tiff";
        }
        if (fileName.equalsIgnoreCase(".bmp")) {
            return "image/bmp";
        }
        if (fileName.equalsIgnoreCase(".wbmp")) {
            return "wbmp/gif";
        }

        if (fileName.equalsIgnoreCase(".mp3")) {
            return "audio/mp3";
        }
        if (fileName.equalsIgnoreCase(".wav")) {
            return "audio/wav";
        }

        if (fileName.equalsIgnoreCase(".html")) {
            return "text/html";
        }
        if (fileName.equalsIgnoreCase(".txt")) {
            return "text/plain";
        }
        if (fileName.equalsIgnoreCase(".xml")) {
            return "text/xml";
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

        return "image/jpg";
    }
}
