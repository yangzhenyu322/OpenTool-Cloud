package com.opentool.ai.tool.controller;

import com.opentool.ai.tool.domain.chat.ChatRequest;
import com.opentool.ai.tool.service.IChatGPTService;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatChoice;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.function.KeyRandomStrategy;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * chat接口类
 * / @Author: ZenSheep
 * / @Date: 2023/10/17 20:50
 */
@Slf4j
@RefreshScope
@RestController
@RequestMapping("/chatgpt")
public class ChatGPTController {
    @Autowired
    private IChatGPTService chatGPTService;

    /**
     * 创建sse连接
     * @param uid
     * @return SseEmitter
     */
    @GetMapping("/createSse/{uid}")
    public SseEmitter createSseConnect(@PathVariable("uid") String uid){
        log.info("[{}]开始创建sse连接", uid);
        return chatGPTService.createSee(uid);
    }

    /**
     * 关闭sse连接
     * @param uid 用户id
     */
    @GetMapping("/closeSse/{uid}")
    public String closeConnect(@PathVariable("uid") String uid) {
        chatGPTService.closeSee(uid);
        return "关闭后端sse连接成功:" + uid;
    }

    /**
     * 聊天接口
     * @param chatRequest 请求参数
     * @return tokens 问题的token长度
     */
    @PostMapping("/message")
    public Long sseChat(@RequestBody ChatRequest chatRequest) {
        log.info("[{}]请求提问：[{}]",chatRequest.getUid(), chatRequest.getQuestion());
        return chatGPTService.sseChat(chatRequest.getUid(), chatRequest.getQuestion());
    }

    public static void main(String[] args) {
//        // 一、流式对话
//        // 不要在生产或测试环境打开BODY级别的日志！！！
//        // 生产或测试环境建议设置这三种级别：NONE,BASIC,HEADERS !!!
//        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
//        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
//        OkHttpClient okHttpClient = new OkHttpClient
//                .Builder()
//                .addInterceptor(httpLoggingInterceptor) // 自定义日志
//                .connectTimeout(30, TimeUnit.SECONDS) // 自定义超时时间
//                .writeTimeout(30, TimeUnit.SECONDS) // 自定义超时时间
//                .readTimeout(30, TimeUnit.SECONDS) // 自定义超时时间
//                .build();
//        OpenAiStreamClient client = OpenAiStreamClient.builder()
//                // apiKey
//                .apiKey(Arrays.asList("sk-CMAaXEQfolbfguToC0E89d26626645CfAc8eD3D700CdE266"))
//                // 自定义Key的获取策略：默认KeyRandomStrategy
//                .keyStrategy(new KeyRandomStrategy())
//                .okHttpClient(okHttpClient)
//                // 自己做了代理就传代理地址，没有可不传
//                .apiHost("https://api.qqslyx.com/")
//                .build();
//
//        // 聊天模型：gpt-3.5
//        ConsoleEventSourceListener eventSourceListener = new ConsoleEventSourceListener();
//        Message message = Message.builder().role(Message.Role.USER).content("你好啊我的伙伴").build();
//        ChatCompletion chatCompletion = ChatCompletion.builder()
//                .messages(Arrays.asList(message))
//                .build();
//
//        // CountDownLatch 的核心思想是，你可以创建一个 CountDownLatch 对象，初始化它的计数值，然后在多个线程中调用 countDown() 方法来递减计数值，最后一个完成的线程会触发等待的线程继续执行。
//        CountDownLatch latch = new CountDownLatch(1);
//        client.streamChatCompletion(chatCompletion,eventSourceListener);
//
//        try {
//            latch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // 2、阻塞式对话
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .addInterceptor(httpLoggingInterceptor) // 自定义日志
                .connectTimeout(30, TimeUnit.SECONDS) // 自定义超时时间
                .writeTimeout(30, TimeUnit.SECONDS) // 自定义超时时间
                .readTimeout(30, TimeUnit.SECONDS) // 自定义超时时间
                .build();
        OpenAiClient openAiClient = OpenAiClient.builder()
                // apiKey
                .apiKey(Arrays.asList("sk-CMAaXEQfolbfguToC0E89d26626645CfAc8eD3D700CdE266"))
                // 自定义Key的获取策略：默认KeyRandomStrategy
                .keyStrategy(new KeyRandomStrategy())
                .okHttpClient(okHttpClient)
                // 自己做了代理就传代理地址，没有可不传
                .apiHost("https://api.qqslyx.com/")
                .build();

        // 聊天模型：gpt-3.5
        Message message = Message.builder().role(Message.Role.USER).content("帮我写一首七字绝诗").build();
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .messages(Arrays.asList(message))
                .build();
        ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletion(chatCompletion);  // 阻塞

        chatCompletionResponse.getChoices().forEach(e -> {
            System.out.println("result:" + e.getMessage().getContent());
        });

        String answer = "";
        List<ChatChoice> choices = chatCompletionResponse.getChoices();
        for(ChatChoice chatChoice: choices) {
            answer += chatChoice.getMessage().getContent();
        }
        System.out.println("answer:" + answer);

        // 自己开代理Demo
//        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890));
//        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
//        // 不要在生产或测试环境打开BODY级别的日志！！！
//        // 生产或测试环境建议设置这三种级别：NONE,BASIC,HEADERS !!!
//        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
//        OkHttpClient okHttpClient = new OkHttpClient
//                .Builder()
//                .proxy(proxy) // 自定义代理
//                .addInterceptor(httpLoggingInterceptor) // 自定义日志
//                .connectTimeout(30, TimeUnit.SECONDS) // 自定义超时时间
//                .writeTimeout(30, TimeUnit.SECONDS) // 自定义超时时间
//                .readTimeout(30, TimeUnit.SECONDS) // 自定义超时时间
//                .build();
//        OpenAiStreamClient client = OpenAiStreamClient.builder()
//                // apiKey
//                .apiKey(Arrays.asList("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"))
//                // 自定义Key的获取策略：默认KeyRandomStrategy
//                .keyStrategy(new KeyRandomStrategy())
//                .okHttpClient(okHttpClient)
//                .build();
//
//        // 聊天模型：gpt-3.5
//        ConsoleEventSourceListener eventSourceListener = new ConsoleEventSourceListener();
//        Message message = Message.builder().role(Message.Role.USER).content("你好，帮我用java实现一个快速排序算法").build();
//        ChatCompletion chatCompletion = ChatCompletion.builder()
//                .messages(Arrays.asList(message))
//                .build();
//
//        // CountDownLatch 的核心思想是，你可以创建一个 CountDownLatch 对象，初始化它的计数值，然后在多个线程中调用 countDown() 方法来递减计数值，最后一个完成的线程会触发等待的线程继续执行。
//        CountDownLatch latch = new CountDownLatch(1);
//        client.streamChatCompletion(chatCompletion,eventSourceListener);
//
//        try {
//            latch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
