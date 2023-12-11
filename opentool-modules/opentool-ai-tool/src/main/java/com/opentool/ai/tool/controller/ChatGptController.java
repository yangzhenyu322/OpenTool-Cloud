package com.opentool.ai.tool.controller;

import com.opentool.ai.tool.domain.vo.ChatRequest;
import com.opentool.ai.tool.service.IChatGptService;
import com.opentool.ai.tool.service.ISseService;
import com.opentool.ai.tool.utils.ChatGptModelUtils;
import com.opentool.common.core.domain.R;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.*;
import com.unfbx.chatgpt.function.KeyRandomStrategy;
import com.unfbx.chatgpt.interceptor.DynamicKeyOpenAiAuthInterceptor;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import com.unfbx.chatgpt.interceptor.OpenAiResponseInterceptor;
import com.unfbx.chatgpt.sse.ConsoleEventSourceListener;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
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
public class ChatGptController {
    @Autowired
    private IChatGptService chatGptService;
    @Autowired
    private ISseService sseService;

    /**
     * 创建sse连接
     * @param uid
     * @return SseEmitter
     */
    @GetMapping("/createSse/{uid}")
    public SseEmitter createSseConnect(@PathVariable("uid") String uid){
        return sseService.createSee(uid);
    }

    /**
     * 关闭sse连接
     * @param uid 用户id
     */
    @GetMapping("/closeSse/{uid}")
    public String closeConnect(@PathVariable("uid") String uid) {
        return sseService.closeSee(uid);
    }

    /**
     * 聊天接口
     * @param chatRequest 请求参数
     * @return tokens 问题的token长度
     */
    @PostMapping("/question")
    public R<?> sseChat(@RequestBody ChatRequest chatRequest) {
        log.info("[{}]-[{}]-[{}]请求提问：[{}]",chatRequest.getUid(), chatRequest.getWid(), chatRequest.getModel(), chatRequest.getQuestion());
        return R.ok(chatGptService.sseChat(chatRequest, ChatGptModelUtils.getStrategyType(chatRequest.getModel())));
    }

    /**
     * 获取历史对话
     * @param uid
     * @param wid
     * @return
     */
    @GetMapping("/history")
    public R<?> getHistoryLog(@RequestParam("uid") String uid, @RequestParam("wid") String wid, @RequestParam("model") String model) {
        return R.ok(chatGptService.getHistoryList(uid, wid, ChatGptModelUtils.getStrategyType(model)));
    }

    @DeleteMapping("/reset")
    public R<?> cleanHistoryLog(@RequestParam("uid") String uid, @RequestParam("wid") String wid, @RequestParam("model") String model) {
        return R.ok(chatGptService.cleanHistoryLog(uid, wid, ChatGptModelUtils.getStrategyType(model)));
    }

    @PostMapping("/upload")
    public R<?> uploadFile(@RequestPart("file") MultipartFile file, @RequestParam("width") int width, @RequestParam("height") int height) {
        return R.ok(chatGptService.uploadFile(file, width, height));
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
//        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
//        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
//        OkHttpClient okHttpClient = new OkHttpClient
//                .Builder()
//                .addInterceptor(httpLoggingInterceptor) // 自定义日志
//                .connectTimeout(300, TimeUnit.SECONDS) // 自定义超时时间
//                .writeTimeout(300, TimeUnit.SECONDS) // 自定义超时时间
//                .readTimeout(300, TimeUnit.SECONDS) // 自定义超时时间
//                .build();
//        OpenAiClient openAiClient = OpenAiClient.builder()
//                // apiKey
//                .apiKey(Arrays.asList("sk-CMAaXEQfolbfguToC0E89d26626645CfAc8eD3D700CdE266"))
//                // 自定义Key的获取策略：默认KeyRandomStrategy
//                .keyStrategy(new KeyRandomStrategy())
//                .okHttpClient(okHttpClient)
//                // 自己做了代理就传代理地址，没有可不传
//                .apiHost("https://api.qqslyx.com/")
//                .build();
//
//        // 聊天
//        Message message = Message.builder().role(Message.Role.USER).content("以长沙的春天为题作画").build();
//        ChatCompletion chatCompletion = ChatCompletion
//                .builder()
//                .messages(Arrays.asList(message))
//                .model(ChatCompletion.Model.GPT_4_1106_PREVIEW.getName())
//                .build();
//        ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletion(chatCompletion);  // 阻塞
//
//        chatCompletionResponse.getChoices().forEach(e -> {
//            System.out.println("result:" + e.getMessage().getContent());
//        });
//
//        String answer = "";
//        List<ChatChoice> choices = chatCompletionResponse.getChoices();
//        for(ChatChoice chatChoice: choices) {
//            answer += chatChoice.getMessage().getContent();
//        }
//        System.out.println("answer:" + answer);

        // 3.附加图片的chatCompletion（gpt4-vision-preview）:流式请求
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .addInterceptor(httpLoggingInterceptor) // 自定义日志
                .addInterceptor(new OpenAiResponseInterceptor())
                .connectTimeout(30, TimeUnit.SECONDS) // 自定义超时时间
                .writeTimeout(30, TimeUnit.SECONDS) // 自定义超时时间
                .readTimeout(30, TimeUnit.SECONDS) // 自定义超时时间
                .build();
        OpenAiStreamClient streamClient = OpenAiStreamClient.builder()
                // apiKey
                .apiKey(Arrays.asList("sk-GxoeUJ0L8vOjWRSP0eAbD53e4fA8436fB894Aa4c4f9f1220"))
                // 自定义Key的获取策略：默认KeyRandomStrategy
                .keyStrategy(new KeyRandomStrategy())
                .authInterceptor(new DynamicKeyOpenAiAuthInterceptor())
                .okHttpClient(okHttpClient)
                // 自己做了代理就传代理地址，没有可不传
                .apiHost("https://api.qqslyx.com/")
                .build();

        Content textContent0 = Content.builder().text("你是一个图像助手，能够对用户提供图片进行分析").type(Content.Type.TEXT.getName()).build();
        List<Content> contentList0 = new ArrayList<>();
        contentList0.add(textContent0);
        MessagePicture message0 = MessagePicture.builder().role(Message.Role.SYSTEM).content(contentList0).build();

        Content textContent = Content.builder().text("帮我描述这张图片").type(Content.Type.TEXT.getName()).build();
        ImageUrl imageUrl = ImageUrl
                .builder()
                .url("https://opentool.oss-cn-shenzhen.aliyuncs.com/ImageConvert/images/origin/8de68c44-61b3-4418-b020-0c4c843bc5ed/girl05.jpg")
                .build();
        Content imageContent = Content.builder().imageUrl(imageUrl).type(Content.Type.IMAGE_URL.getName()).build();
        List<Content> contentList = new ArrayList<>();
        contentList.add(textContent);
        contentList.add(imageContent);
        MessagePicture message = MessagePicture.builder().role(Message.Role.USER).content(contentList).build();

        List<MessagePicture> messagePictureList = new ArrayList<>();
        messagePictureList.add(message0);
        messagePictureList.add(message);

        ChatCompletionWithPicture chatCompletion = ChatCompletionWithPicture
                .builder()
                .messages(messagePictureList)
                .model(ChatCompletion.Model.GPT_4_VISION_PREVIEW.getName())
                .build();
        streamClient.streamChatCompletion(chatCompletion, new ConsoleEventSourceListener());
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 4.附加图片的chatCompletion（gpt4-vision-preview）:阻塞式请求
//        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
//        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
//        OkHttpClient okHttpClient = new OkHttpClient
//                .Builder()
//                .addInterceptor(httpLoggingInterceptor) // 自定义日志
//                .addInterceptor(new OpenAiResponseInterceptor())
//                .connectTimeout(30, TimeUnit.SECONDS) // 自定义超时时间
//                .writeTimeout(30, TimeUnit.SECONDS) // 自定义超时时间
//                .readTimeout(30, TimeUnit.SECONDS) // 自定义超时时间
//                .build();
//        OpenAiClient client = OpenAiClient.builder()
//                // apiKey
//                .apiKey(Arrays.asList("sk-GxoeUJ0L8vOjWRSP0eAbD53e4fA8436fB894Aa4c4f9f1220"))
//                // 自定义Key的获取策略：默认KeyRandomStrategy
//                .keyStrategy(new KeyRandomStrategy())
//                .okHttpClient(okHttpClient)
//                // 自己做了代理就传代理地址，没有可不传
//                .apiHost("https://api.qqslyx.com/")
//                .build();
//        Content textContent = Content.builder().text("What’s in this image?").type(Content.Type.TEXT.getName()).build();
//        ImageUrl imageUrl = ImageUrl
//                .builder()
//                .url("https://opentool.oss-cn-shenzhen.aliyuncs.com/ImageConvert/images/origin/8de68c44-61b3-4418-b020-0c4c843bc5ed/girl05.jpg")
//                .build();
//        Content imageContent = Content.builder().imageUrl(imageUrl).type(Content.Type.IMAGE_URL.getName()).build();
//        List<Content> contentList = new ArrayList<>();
//        contentList.add(textContent);
//        contentList.add(imageContent);
//        MessagePicture message = MessagePicture.builder().role(Message.Role.USER).content(contentList).build();
//        ChatCompletionWithPicture chatCompletion = ChatCompletionWithPicture
//                .builder()
//                .messages(Collections.singletonList(message))
//                .model(ChatCompletion.Model.GPT_4_VISION_PREVIEW.getName())
//                .build();
//        ChatCompletionResponse chatCompletionResponse = client.chatCompletion(chatCompletion);
//        chatCompletionResponse.getChoices().forEach(e -> System.out.println(e.getMessage()));
    }
}
