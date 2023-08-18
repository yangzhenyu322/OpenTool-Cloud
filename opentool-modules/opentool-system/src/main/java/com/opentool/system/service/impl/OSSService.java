package com.opentool.system.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.opentool.system.config.OSSConfiguration;
import com.opentool.system.service.IOSSService;
import com.opentool.system.utils.file.OSSFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * OSS服务类
 * / @Author: ZenSheep
 * / @Date: 2023/8/10 16:05
 */
@Service
public class OSSService implements IOSSService {
    @Autowired
    private OSS ossClient;

    @Autowired
    private OSSConfiguration ossConfiguration;

    /**
     * 上传文件到阿里云 OSS 服务器
     * 链接：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/upload_object.html?spm=5176.docoss/user_guide/upload_object
     *
     * @param file 文件
     * @param storagePath 文件存储路径
     * @return 文件存储完整路径
     */
    @Override
    public String uploadFile(MultipartFile file, String storagePath) {
        String url = "";
        try {
            // UUID生成文件名，防止重复
            String fileName = "";
            String baseName = OSSFileUtils.getBaseName(file.getOriginalFilename());
            InputStream inputStream = file.getInputStream();
            // 创建ObjectMetadata，设置用户自定义的元数据以及HTTP头，比如内容长度，ETag等
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(inputStream.available());
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");
            objectMetadata.setContentType(OSSFileUtils.getcontentType(file.getOriginalFilename()));
            objectMetadata.setContentDisposition("inline;filename=" + baseName);
            fileName = storagePath + "/" + UUID.randomUUID().toString() + "/"  + file.getOriginalFilename();
            // 上传文件：调用ossClient的putObject方法完成文件上传，并返回文件名
            ossClient.putObject(ossConfiguration.getBucketName(), fileName, inputStream, objectMetadata);
            // 设置签名URL过期时间，单位为毫秒。
            Date expiration = new Date(new Date().getTime() + 3600 * 1000);
            // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
            url = ossClient.generatePresignedUrl(ossConfiguration.getBucketName(), fileName, expiration).toString();
//            url = ossClient.generatePresignedUrl(ossConfiguration.getBucketName(), fileName, null).toString();
            // 处理返回的url格式
            url = url.substring(0, url.lastIndexOf("?"));
            url = url.replaceAll("http","https");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return url;
    }
}
