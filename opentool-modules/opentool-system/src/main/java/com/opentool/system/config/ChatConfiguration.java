package com.opentool.system.config;

import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.function.KeyRandomStrategy;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/10/18 19:53
 */
@Configuration
public class ChatConfiguration {
    // 代理方式二选一
    // 1.自定义代理服务
    @Value("${chatgpt.proxy.httpHost}")
    private String httpHost; // 自己的代理服务器IP

    @Value("${chatgpt.proxy.port}")
    private int proxyPort; // 自己代理服务端口

    // 2.中转代理
    @Value("${chatgpt.apiHost}")
    private String apiHost;

    // api key
    @Value("${chatgpt.apiKey}")
    private String apiKey;
    // 连接超时
    @Value("${chatgpt.timeout.connect}")
    private int connectTimeout;
    // write超时
    @Value("${chatgpt.timeout.write}")
    private int writeTimeout;
    // read超时
    @Value("${chatgpt.timeout.read}")
    private int readTimeout;

    @Bean OkHttpClient okHttpClient() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        // 生产或测试环境建议设置这三种级别：NONE,BASIC,HEADERS（不要BODYS）
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        return new OkHttpClient
                .Builder()
                .addInterceptor(httpLoggingInterceptor) // 自定义日志
                .connectTimeout(connectTimeout, TimeUnit.SECONDS) // 自定义超时时间
                .writeTimeout(writeTimeout, TimeUnit.SECONDS) // 自定义超时时间
                .readTimeout(readTimeout, TimeUnit.SECONDS) // 自定义超时时间
                .build();
    }

    /**
     * 阻塞式传输
     * @return
     */
//    @Bean
//    public OpenAiClient openAiClient() {
//        return OpenAiClient.builder()
//                .apiKey(Arrays.asList(apiKey))
//                .keyStrategy(new KeyRandomStrategy())
//                .authInterceptor(new DynamicKeyOpenAiAuthInterceptor())
//                .okHttpClient(okHttpClient())
//                .apiHost(apiHost)
//                .build();
//    }

    /**
     * 流式传输
     * @param okHttpClient
     * @return
     */
    @Bean
    public OpenAiStreamClient openAiStreamClient(OkHttpClient okHttpClient) {
        return OpenAiStreamClient.builder()
                // apiKey
                .apiKey(Arrays.asList(apiKey))
                // 自定义Key的获取策略：默认KeyRandomStrategy
                .keyStrategy(new KeyRandomStrategy())
                .okHttpClient(okHttpClient)
                // 自己做了dialing就传代理地址，没有可不传
                .apiHost(apiHost)
                .build();
    }
}
