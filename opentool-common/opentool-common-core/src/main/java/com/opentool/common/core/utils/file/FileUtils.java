package com.opentool.common.core.utils.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件处理工具类
 * @author ZenSheep
 */
@Slf4j
public class FileUtils {


    /**
     * 获取文件名（不包含后缀）
     * @param fileName 元文件名（包含后缀）
     * @return
     */
    public static String getBaseName(String fileName) {
        return FilenameUtils.getBaseName(fileName);
    }

    /**
     * url文件路径转File对象，返回本地文件路径（方便后续删除缓存文件）
     * @param url 文件url路径
     * @param fileName 文件名
     * @return 文件本地路径
     * @throws IOException
     */
    public static String urlToFilePath(String url, String fileName) throws IOException {
        String tempFileName = "./temp/url/file/" + fileName;
        // 创建路径中的文件夹
        new File(tempFileName).getParentFile().mkdirs();
        BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(tempFileName);
        byte dataBuffer[] = new byte[1024];
        int bytesRead;
        while((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
            fileOutputStream.write(dataBuffer, 0, bytesRead);
        }
        in.close();
        fileOutputStream.close();

        return tempFileName;
    }

    /**
     * 文件本地路径转File对象
     * @param pathName 本地文件路径
     * @return File对象
     */
    public static File filePathToFile(String pathName) {
        File file = new File(pathName);
        // 创建路径中的文件夹
        file.getParentFile().mkdirs();
        return file;
    }

    /**
     * 删除文件
     * @param filePath 文件
     * @return 是否删除成功：boolean
     */
    public static boolean deleteFile(String filePath) {
        boolean flag = false;
        File file = new File(filePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            flag = file.delete();
        }

        if (flag) {
            log.info("本地文件删除成功");
        } else {
            log.error("本地文件删除失败");
        }
        return flag;
    }


    /**
     * 保证文件名唯一
     * @param names 一般文件名：girl.png
     * @return
     */
    public static String[] getUniFileNames(String[] names) {
        int n = names.length;

        String[] baseNames = new String[n];
        String[] suffixTypes = new String[n];
        for (int i = 0; i < n; i++) {
            int lastDotIndex = names[i].lastIndexOf(".");
            baseNames[i] = names[i].substring(0, lastDotIndex);
            suffixTypes[i] = names[i].substring(lastDotIndex);
        }

        Map<String, Integer> index = new HashMap<String, Integer>();

        String[] res = new String[n];
        for (int i = 0; i < n; i++) {
            String name = baseNames[i];
            if (!index.containsKey(name)) {
                res[i] = name;
                index.put(name, 1);
            } else {
                int k = index.get(name);
                while (index.containsKey(addSuffix(name, k))) {
                    k++;
                }
                res[i] = addSuffix(name, k);
                index.put(name, k + 1);
                index.put(addSuffix(name, k), 1);
            }
        }

        // 对新文件列表添加文件类型后缀
        for (int i = 0; i < n; i++) {
            res[i] += suffixTypes[i];
        }

        return res;
    }

    /**
     * 添加括号后缀
     * @param name
     * @param k
     * @return
     */
    public static String addSuffix(String name, int k) {
        return name + "(" + k + ")";
    }

//    // 斜杠
//    public static final char SLASH = '/';
//
//    // 反斜杠
//    public static final char BACKSLASH = '\\';
//
//    public static String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+";

//    /**
//     * 输出指定文件的byte数组
//     * @param filePath 文件路径
//     * @param os 输出流
//     * @return
//     */
//    public static void writeBytes(String filePath, OutputStream os) throws IOException {
//        FileInputStream fis = null;
//        try {
//            File file = new File(filePath);
//            if(!file.exists()) {
//                throw new FileNotFoundException(filePath);
//            }
//            fis = new FileInputStream(file);
//            byte[] b = new byte[1024];
//            int length;
//            while ( (length = fis.read(b)) > 0) {
//                os.write(b, 0, length);
//            }
//        } catch (IOException e) {
//            throw e;
//        }
//        finally {
//            if (os != null) {
//                try {
//                    os.close();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//            }
//            if (fis != null) {
//                try {
//                    fis.close();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//            }
//        }
//    }



//    /**
//     * 文件名验证
//     * @param filename 文件名称
//     * @return true 正常 false 非法
//     */
//    public static boolean isValidFileName(String filename) {
//        return filename.matches(FILENAME_PATTERN);
//    }

//    /**
//     * 检查文件是否可下载
//     * @param resource 需要下载的文件
//     * @return true 正常 false 非法
//     */
//    public static boolean checkAllowDownload(String resource) {
//        // 禁止目录上跳级别
//        if (resource.contains("..")) {
//            return false;
//        }
//
//        // 判断是否允许在下载的文件规则内
//        return ArrayUtils.contains(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION, FileTypeUtils.getFileType(resource));
//    }

//    /**
//     * 下载文件名重新编码
//     *
//     * @param request 请求对象
//     * @param fileName 文件名
//     * @return 编码后的文件名
//     */
//    public static String setFileDownloadHeader(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
//        final String agent = request.getHeader("USER-AGENT");
//        String filename = fileName;
//        if (agent.contains("MSIE")) {
//            // IE浏览器
//            filename = URLEncoder.encode(filename, "utf-8");
//            filename = filename.replace("+", " ");
//        } else if (agent.contains("Firefox")) {
//            // 火狐浏览器
//            filename = new String(fileName.getBytes(), "ISO8859-1");
//        } else if (agent.contains("Chrome")) {
//            // google浏览器
//            filename = URLEncoder.encode(filename, "utf-8");
//        } else {
//            // 其它浏览器
//            filename = URLEncoder.encode(filename, "utf-8");
//        }
//        return filename;
//    }

//    /**
//     * 返回文件名
//     * @param filePath 文件
//     * @return 文件名
//     */
//    public static String getName(String filePath) {
//        if (null == filePath) {
//            return null;
//        }
//        int len = filePath.length();
//        if (0 == len) {
//            return filePath;
//        }
//        if (isFileSeparator(filePath.charAt(len - 1))) {
//            // 以分隔符结尾的去掉结尾分隔符
//            len--;
//        }
//
//        int begin = 0;
//        char c;
//        for (int i = len - 1; i > -1; i--) {
//            c = filePath.charAt(i);
//            if (isFileSeparator(c)) {
//                // 查找最后一个路径分隔符(/ 或者 \)
//                begin = i + 1;
//                break;
//            }
//        }
//
//        return filePath.substring(begin, len);
//    }



//    /**
//     * 是否为Windows或者Linux（Unix）文件分隔符
//     * Windows平台下的分隔符为\. Linux（Unix）为/
//     * @param c 字符
//     * @return 是否为Windows或者Linux（Unix）文件分隔符
//     */
//    public static boolean isFileSeparator(char c) {
//        return SLASH == c || BACKSLASH == c;
//    }

//    /**
//     * 下载文件名重新编码
//     *
//     * @param response 响应对象
//     * @param realFileName 真实文件名
//     * @return
//     */
//    public static void setAttachmentResponseHeader(HttpServletResponse response, String realFileName) throws UnsupportedEncodingException
//    {
//        String percentEncodedFileName = percentEncode(realFileName);
//
//        StringBuilder contentDispositionValue = new StringBuilder();
//        contentDispositionValue.append("attachment; filename=")
//                .append(percentEncodedFileName)
//                .append(";")
//                .append("filename*=")
//                .append("utf-8''")
//                .append(percentEncodedFileName);
//
//        response.setHeader("Content-disposition", contentDispositionValue.toString());
//        response.setHeader("download-filename", percentEncodedFileName);
//    }

//    /**
//     * 百分号编码工具方法
//     *
//     * @param s 需要百分号编码的字符串
//     * @return 百分号编码后的字符串
//     */
//    public static String percentEncode(String s) throws UnsupportedEncodingException
//    {
//        String encode = URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
//        return encode.replaceAll("\\+", "%20");
//    }
}
