package com.opentool.system.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * OSS-Client配置类
 * / @Author: ZenSheep
 * / @Date: 2023/8/10 15:48
 */
@Configuration
public class OSSConfiguration {
    private static String accessKeyId;

    private static String accessKeySecret;

    private static String endpoint;

    private static String bucketName;

    // 静态变量值无法通过@Value直接注入，需要使用公共set方法注入
    @Value("${aliyun.accessKeyId}")
    public void setAccessKeyId(String accessKeyId) {
        OSSConfiguration.accessKeyId = accessKeyId;
    }

    @Value("${aliyun.accessKeySecret}")
    public void setAccessKeySecret(String accessKeySecret) {
        OSSConfiguration.accessKeySecret = accessKeySecret;
    }

    @Value("${aliyun.oss.endpoint}")
    public void setEndpoint(String endpoint) {
        OSSConfiguration.endpoint = endpoint;
    }

    @Value("${aliyun.oss.bucketName}")
    public void setBucketName(String bucketName) {
        OSSConfiguration.bucketName = bucketName;
    }

    public static String getBucketName() {
        return OSSConfiguration.bucketName;
    }

    // 单例模式下使用volatile是为了禁止重排序，防止instance = new Instance() 出现问题
    private volatile static OSS ossClient;

    private volatile static OSSClientBuilder ossClientBuilder;

    @Bean
    // 在使用不同产品时容易出现线程安全问题，解决方法升级到SDK 2.0：https://help.aliyun.com/zh/sdk/product-overview/differences-between-v1-and-v2-sdks
    @Scope("prototype")
    public static OSS initOSSClient() {
        if (ossClient == null) {
            synchronized (OSSConfiguration.class) {
                if (ossClient == null) {
                    ossClient = initOSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
                }
            }
        }
        return ossClient;
    }

    public static OSSClientBuilder initOSSClientBuilder() {
        if (ossClientBuilder == null) {
            synchronized (OSSConfiguration.class) {
                if (ossClientBuilder == null) {
                    ossClientBuilder = new OSSClientBuilder();
                }
            }
        }
        return ossClientBuilder;
    }
}
