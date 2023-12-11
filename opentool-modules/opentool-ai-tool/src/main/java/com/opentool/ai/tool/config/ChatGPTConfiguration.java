package com.opentool.ai.tool.config;

import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.function.KeyRandomStrategy;
import com.unfbx.chatgpt.interceptor.DynamicKeyOpenAiAuthInterceptor;
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
public class ChatGPTConfiguration {
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
    @Value("${chatgpt.apiKey3_5}")
    private String apiKey3_5;
    @Value("${chatgpt.apiKey4}")
    private String apiKey4;
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
    @Bean
    public OpenAiClient openAiClient3_5() {
        return OpenAiClient.builder()
                .apiKey(Arrays.asList(apiKey3_5))
                .keyStrategy(new KeyRandomStrategy())
                .authInterceptor(new DynamicKeyOpenAiAuthInterceptor())
                .okHttpClient(okHttpClient())
                .apiHost(apiHost)
                .build();
    }

    /**
     * 流式传输
     * @param okHttpClient
     * @return
     */
    @Bean
    public OpenAiStreamClient openAiStreamClient3_5(OkHttpClient okHttpClient) {

        return OpenAiStreamClient.builder()
                // apiKey
                .apiKey(Arrays.asList(apiKey3_5))
                // 自定义Key的获取策略：默认KeyRandomStrategy
                .keyStrategy(new KeyRandomStrategy())
                .authInterceptor(new DynamicKeyOpenAiAuthInterceptor())
                .okHttpClient(okHttpClient)
                // 自己做了代理就传代理地址，没有可不传
                .apiHost(apiHost)
                .build();
    }

    /**
     * 阻塞式传输
     * @return
     */
    @Bean
    public OpenAiClient openAiClient4() {
        return OpenAiClient.builder()
                .apiKey(Arrays.asList(apiKey4))
                .keyStrategy(new KeyRandomStrategy())
                .authInterceptor(new DynamicKeyOpenAiAuthInterceptor())
                .okHttpClient(okHttpClient())
                .apiHost(apiHost)
                .build();
    }

    /**
     * 流式传输
     * @param okHttpClient
     * @return
     */
    @Bean
    public OpenAiStreamClient openAiStreamClient4(OkHttpClient okHttpClient) {
        return OpenAiStreamClient.builder()
                // apiKey
                .apiKey(Arrays.asList(apiKey4))
                // 自定义Key的获取策略：默认KeyRandomStrategy
                .keyStrategy(new KeyRandomStrategy())
                .authInterceptor(new DynamicKeyOpenAiAuthInterceptor())
                .okHttpClient(okHttpClient)
                // 自己做了dialing就传代理地址，没有可不传
                .apiHost(apiHost)
                .build();
    }
}