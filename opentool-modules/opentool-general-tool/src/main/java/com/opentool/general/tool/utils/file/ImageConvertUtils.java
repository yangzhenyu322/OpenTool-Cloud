package com.opentool.general.tool.utils.file;

import com.opentool.common.core.utils.file.FileUtils;
import com.opentool.general.tool.domain.vo.ConvertConfigInfo;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 图像格式转换工具类
 * / @Author: ZenSheep
 * / @Date: 2023/8/17 14:22
 */
public class ImageConvertUtils {

    // 图像格式转换目标支持类型(包含小写)：JPG JPEG PNG GIF BMP WBMP
    private final static String[] IMG_SUPPORT_TYPE = {"jpg", "JPG", "jpeg","JPEG", "png", "PNG", "gif", "GIF", "bmp", "BMP", "wbmp", "WBMP"};
    private final static String TEMP_FILE_PATH = "./temp/imageConvert/image/";

    /**
     * 图像格式转换
     * @param urlStrList url字符串数组
     * @param targetFormat 目标格式，如PNG
     * @return
     * @throws IOException
     */
    public static List<File> urlsFormatConvert(List<String> urlStrList, String targetFormat, ConvertConfigInfo convertConfigInfo) throws IOException {
        List<File> fileList = new ArrayList<>(); // 图片本地缓存地址
        List<URL> urlList = new ArrayList<>(); // URL对象List

        List<String> fileNameList = new ArrayList<>(); // 文件名列表
        for (String urlStr :urlStrList) {
            urlList.add(new URL(urlStr));

            String imgName = urlStr.substring(urlStr.lastIndexOf("/") + 1); // 如 girl.jpg
            fileNameList.add(imgName);
        }

        // 文件名重复检测
        fileNameList = Arrays.asList(FileUtils.getUniFileNames(fileNameList.toArray(new String[0])));
        for (String fileName : fileNameList) {
            String baseName = FilenameUtils.getBaseName(fileName);
            fileList.add(new File(TEMP_FILE_PATH + baseName + "." + targetFormat.toLowerCase()));
        }

        if (Arrays.asList(IMG_SUPPORT_TYPE).contains(targetFormat)) { // 支持目标格式
            // 转换为指定目标格式和尺寸格式
            switch (convertConfigInfo.getSizeType()) {
                case "NoChange": {
                    Thumbnails.fromURLs(urlList)
                            .scale(1D)
                            .allowOverwrite(true)
                            .outputFormat(targetFormat)
                            .toFiles(fileList);
                    break;
                }
                case "Size": {
                    Thumbnails.fromURLs(urlList)
                            .forceSize(convertConfigInfo.getWidth(), convertConfigInfo.getHeight()) // 不保证宽高比改变大小
                            .allowOverwrite(true)
                            .outputFormat(targetFormat)
                            .toFiles(fileList);
                    break;
                }
                case "Width": {
                    Thumbnails.fromURLs(urlList)
                            .width(convertConfigInfo.getWidth()) // 改变宽度
                            .keepAspectRatio(false) // 不保证宽高比
                            .allowOverwrite(true)
                            .outputFormat(targetFormat)
                            .toFiles(fileList);
                    break;
                }
                case "WidthKeepRatio": {
                    Thumbnails.fromURLs(urlList)
                            .width(convertConfigInfo.getWidth()) // 改变宽度同时保证宽高比
                            .allowOverwrite(true)
                            .outputFormat(targetFormat)
                            .toFiles(fileList);
                    break;
                }
                case "Height": {
                    Thumbnails.fromURLs(urlList)
                            .height(convertConfigInfo.getHeight()) // 改变高度
                            .keepAspectRatio(false) // 不保证宽高比
                            .allowOverwrite(true)
                            .outputFormat(targetFormat)
                            .toFiles(fileList);
                    break;
                }
                case "HeightKeepRatio": {
                    Thumbnails.fromURLs(urlList)
                            .height(convertConfigInfo.getHeight()) // 改变高度同时保证宽高比
                            .allowOverwrite(true)
                            .outputFormat(targetFormat)
                            .toFiles(fileList);
                    break;
                }
                case "Scale": {
                    Thumbnails.fromURLs(urlList)
                            .scale(convertConfigInfo.getScale()) // 按比例缩放图像
                            .allowOverwrite(true)
                            .outputFormat(targetFormat)
                            .toFiles(fileList);
                    break;
                }
                default: {
                    System.out.println("未改变图像格式");
                    break;
                }
            }
        }

        return fileList;
    }



    /**
     * 改变图片长宽
     * @param urlStrList 原图像Url
     * @return
     * @throws IOException
     */
    public static List<File> urlsSizeConvert(List<String> urlStrList, int width, int height) throws IOException {
        List<File> fileList = new ArrayList<>();
        List<URL> urlList = new ArrayList<>();
        for (String urlStr :urlStrList) {
            String imgName = urlStr.substring(urlStr.lastIndexOf("/") + 1); // 如 girl.jpg
            String baseName = FilenameUtils.getBaseName(imgName);
            String baseFormat = FilenameUtils.getExtension(imgName);
            urlList.add(new URL(urlStr));
            // 图片本地缓存地址
            fileList.add(new File(TEMP_FILE_PATH + baseName + "." + baseFormat));
        }


        Thumbnails.fromURLs(urlList)
                .forceSize(width, height) // 不保证宽高比改变大小
                .allowOverwrite(true)
                .toFiles(fileList);

        return fileList;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        // 功能：设置图片大小、是否允许覆盖、图片质量、旋转、水印、格式转换、单文件处理、多文件处理
        // 参考：https://juejin.cn/post/7197217616490004539

//        File file = new File(new URL("https://opentool.oss-cn-shenzhen.aliyuncs.com/ImageConvert/images/74fbb686-a717-4074-95f9-d591750d5b3d/树屋.png").toURI());

//         本地图片
        Thumbnails.of("F:\\图片\\idea\\girl01.jpg")  // 支持本地图片、网络图片、File的来源
                .scale(0.5D)  // 缩放大小：Double
                .allowOverwrite(true)  // 允许覆盖已存在文件
                .toFile(TEMP_FILE_PATH + "\\girl01-test.jpg");  // 保存至目标路径
//
//        // 网络图片
//        File urlOutPutFile = new File("F:\\JavaProject\\file-save-path\\admin\\upload\\girl03.jpg");
//        Thumbnails.of(new URL("https://opentool.oss-cn-shenzhen.aliyuncs.com/ImageConvert/images/16944503-339f-4c03-a48c-05f502fc7155/girl03.jpg"))
//                .scale(1.5D)
//                .toFile(urlOutputFile);

         // 支持文件夹选择
        // 使用fromFilenames处理多张图片
//        File file = new File("F:\\图片\\idea");  // input为图片存放文件夹路径，例如F:\input
//        File[] files = file.listFiles();
//        assert files != null;
//        List<String> filenames = Arrays.stream(files).map(File::getPath).collect(Collectors.toList());
//        Thumbnails.fromFilenames(filenames)
//                .scale(1D)
//                .allowOverwrite(true)  // 允许覆盖已存在文件
//                // 这里必须使用`toFiles`，因为处理的是多图
//                // output为图片输出文件夹路径，例如F:\output, Rename为图片重命名规则，NO_CHANGE、PREFIX_DOT_THUMBNAIL、SUFFIX_HYPHEN_THUMBNAIL等
//                .toFiles(new File("F:\\图片\\output"), Rename.NO_CHANGE);


        // 使用fromFiles处理多张图片
//        File file1 = new File("F:\\图片\\idea");  // input为图片存放文件夹路径，例如F:\input
//        File[] files1 = file1.listFiles();
//        Thumbnails.fromFiles(Arrays.asList(files1))
//                .scale(1D)
//                .allowOverwrite(true)  // 允许覆盖已存在文件
//                // 这里必须使用`toFiles`，因为处理的是多图
//                // output为图片输出文件夹路径，例如F:\output, Rename为图片重命名规则，NO_CHANGE、PREFIX_DOT_THUMBNAIL、SUFFIX_HYPHEN_THUMBNAIL等
//                .toFiles(new File("F:\\图片\\output"), Rename.NO_CHANGE);

        // 使用fromURLs处理多个图片
//        List<URL> urlList = new ArrayList<>();
//        urlList.add(new URL("https://opentool.oss-cn-shenzhen.aliyuncs.com/ImageConvert/images/4f1227ee-16da-4d9c-a712-04aecc6f6f75/girl01.jpg"));
//        urlList.add(new URL("https://opentool.oss-cn-shenzhen.aliyuncs.com/ImageConvert/images/16944503-339f-4c03-a48c-05f502fc7155/girl03.jpg"));
//        List<File> filesList = new ArrayList<>();
//        filesList.add(new File("F:\\图片\\output\\girl01.jpg"));
//        filesList.add(new File("F:\\图片\\output\\girl03.jpg"));
//        Thumbnails.fromURLs(urlList)
//                .scale(1D)
//                .allowOverwrite(true)  // 允许覆盖已存在文件
//                .toFiles(filesList);

        // 格式转换
//        Thumbnails.of(new URL("https://opentool.oss-cn-shenzhen.aliyuncs.com/ImageConvert/images/74fbb686-a717-4074-95f9-d591750d5b3d/树屋.png"))
//                .scale(1D)
//                .allowOverwrite(true)
//                .outputFormat("JPG") // 支持JPG jpg tiff bmp BMP gif GIF WBMP png PNG JPEG jpeg wbmp
//                .toFile(new File("F:\\图片\\output\\树屋.jpg"));

        // 自定义格式转换函数
//        List<String> urlStrList = new ArrayList<>();
//        urlStrList.add("https://opentool.oss-cn-shenzhen.aliyuncs.com/ImageConvert/images/origin/7db52a82-c468-4d21-983b-b6ddf1dc1c2e/girl01.jpg");
//        String targetFormat = "gif";
//        List<File> fileList = urlsFormatConvert(urlStrList, targetFormat);

        // 图像略缩图
        // File file = imgConvertThumbnail(new File("F:\\图片\\idea\\girl01.jpg"));
    }
}