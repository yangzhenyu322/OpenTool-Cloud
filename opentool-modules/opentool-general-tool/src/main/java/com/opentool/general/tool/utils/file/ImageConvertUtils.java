package com.opentool.general.tool.utils.file;

import net.coobird.thumbnailator.Thumbnails;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * 图像格式转换工具类
 * / @Author: ZenSheep
 * / @Date: 2023/8/17 14:22
 */
public class ImageConvertUtils {

    public static void main(String[] args) throws IOException, URISyntaxException {
        // 功能：设置图片大小、是否允许覆盖、图片质量、旋转、水印、格式转换、单文件处理、多文件处理
        // 参考：https://juejin.cn/post/7197217616490004539

//        File file = new File(new URL("https://opentool.oss-cn-shenzhen.aliyuncs.com/ImageConvert/images/74fbb686-a717-4074-95f9-d591750d5b3d/树屋.png").toURI());

//         本地图片
//        Thumbnails.of("F:\\图片\\idea\\girl01.jpg")  // 支持本地图片、网络图片、File的来源
//                .scale(1D)  // 缩放大小：Double
//                .allowOverwrite(true)  // 允许覆盖已存在文件
//                .toFile("F:\\JavaProject\\file-save-path\\admin\\upload\\girl01.jpg");  // 保存至目标路径

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
        Thumbnails.of(new URL("https://opentool.oss-cn-shenzhen.aliyuncs.com/ImageConvert/images/74fbb686-a717-4074-95f9-d591750d5b3d/树屋.png"))
                .scale(1D)
                .allowOverwrite(true)
                .outputFormat("JPG") // 支持JPG jpg tiff bmp BMP gif GIF WBMP png PNG JPEG tif TIF TIFF jpeg wbmp
                .toFile(new File("F:\\图片\\output\\树屋.jpg"));
    }
}
