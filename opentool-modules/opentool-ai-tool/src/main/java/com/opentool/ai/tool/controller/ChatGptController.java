package com.opentool.ai.tool.controller;

import com.opentool.ai.tool.domain.vo.ChatRequest;
import com.opentool.ai.tool.service.IChatGptService;
import com.opentool.ai.tool.service.ISseService;
import com.opentool.ai.tool.utils.ChatGptModelUtils;
import com.opentool.common.core.domain.R;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.function.KeyRandomStrategy;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import com.unfbx.chatgpt.sse.ConsoleEventSourceListener;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.net.URISyntaxException;
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

    public static void main(String[] args) throws URISyntaxException, IOException {
        // 一、流式对话
        // 不要在生产或测试环境打开BODY级别的日志！！！
        // 生产或测试环境建议设置这三种级别：NONE,BASIC,HEADERS !!!
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .addInterceptor(httpLoggingInterceptor) // 自定义日志
                .connectTimeout(30, TimeUnit.SECONDS) // 自定义超时时间
                .writeTimeout(30, TimeUnit.SECONDS) // 自定义超时时间
                .readTimeout(30, TimeUnit.SECONDS) // 自定义超时时间
                .build();
        OpenAiStreamClient client = OpenAiStreamClient.builder()
                // apiKey
                .apiKey(Arrays.asList("sk-CMAaXEQfolbfguToC0E89d26626645CfAc8eD3D700CdE266"))
                // 自定义Key的获取策略：默认KeyRandomStrategy
                .keyStrategy(new KeyRandomStrategy())
                .okHttpClient(okHttpClient)
                // 自己做了代理就传代理地址，没有可不传
                .apiHost("https://api.qqslyx.com/")
                .build();

        // 聊天模型：gpt-3.5
        ConsoleEventSourceListener eventSourceListener = new ConsoleEventSourceListener();
        Message message = Message.builder().role(Message.Role.USER).content("你好啊我的伙伴").build();
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .messages(Arrays.asList(message))
                .build();

        // CountDownLatch 的核心思想是，你可以创建一个 CountDownLatch 对象，初始化它的计数值，然后在多个线程中调用 countDown() 方法来递减计数值，最后一个完成的线程会触发等待的线程继续执行。
        CountDownLatch latch = new CountDownLatch(1);
        client.streamChatCompletion(chatCompletion,eventSourceListener);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
//        OpenAiStreamClient streamClient = OpenAiStreamClient.builder()
//                // apiKey
//                .apiKey(Arrays.asList("sk-GxoeUJ0L8vOjWRSP0eAbD53e4fA8436fB894Aa4c4f9f1220"))
//                // 自定义Key的获取策略：默认KeyRandomStrategy
//                .keyStrategy(new KeyRandomStrategy())
//                .authInterceptor(new DynamicKeyOpenAiAuthInterceptor())
//                .okHttpClient(okHttpClient)
//                // 自己做了代理就传代理地址，没有可不传
//                .apiHost("https://api.qqslyx.com/")
//                .build();
//
//        Content textContent0 = Content.builder().text("你是一个图像助手，能够对用户提供图片进行分析").type(Content.Type.TEXT.getName()).build();
//        List<Content> contentList0 = new ArrayList<>();
//        contentList0.add(textContent0);
//        MessagePicture message0 = MessagePicture.builder().role(Message.Role.SYSTEM).content(contentList0).build();
//
//        Content textContent = Content.builder().text("帮我描述这张图片").type(Content.Type.TEXT.getName()).build();
//        ImageUrl imageUrl = ImageUrl
//                .builder()
//                .url("https://opentool.oss-cn-shenzhen.aliyuncs.com/ImageConvert/images/origin/8de68c44-61b3-4418-b020-0c4c843bc5ed/girl05.jpg")
//                .build();
//        Content imageContent = Content.builder().imageUrl(imageUrl).type(Content.Type.IMAGE_URL.getName()).build();
//        List<Content> contentList = new ArrayList<>();
//        contentList.add(textContent);
//        contentList.add(imageContent);
//        MessagePicture message = MessagePicture.builder().role(Message.Role.USER).content(contentList).build();
//
//        List<MessagePicture> messagePictureList = new ArrayList<>();
//        messagePictureList.add(message0);
//        messagePictureList.add(message);
//
//        ChatCompletionWithPicture chatCompletion = ChatCompletionWithPicture
//                .builder()
//                .messages(messagePictureList)
//                .model(ChatCompletion.Model.GPT_4_VISION_PREVIEW.getName())
//                .build();
//        streamClient.streamChatCompletion(chatCompletion, new ConsoleEventSourceListener());
//        CountDownLatch countDownLatch = new CountDownLatch(1);
//        try {
//            countDownLatch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

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
//                .url("https://opentool.oss-cn-shenzhen.aliyuncs.com/ChatGpt/image/3dead68b-f7df-4707-bbfc-b53a2270c53b/girl03.jpg")
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

        // 5、Dall-e-3生成图片
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
//                .apiKey(Arrays.asList("sk-CMAaXEQfolbfguToC0E89d26626645CfAc8eD3D700CdE266")) // 3.5和4的key都能调用
//                .okHttpClient(okHttpClient)
//                .apiHost("https://api.qqslyx.com/")
//                .build();
//
//        Image image = Image.builder()
//                .responseFormat(ResponseFormat.URL.getName())
//                .model(Image.Model.DALL_E_3.getName())
//                .prompt("A photograph of a black bird.")
//                .n(1)
//                .quality(Image.Quality.HD.getName())
//                .size(SizeEnum.size_1024.getName())
//                .style(Image.Style.NATURAL.getName())
//                .build();
//        ImageResponse imageResponse = client.genImages(image);
//        System.out.println("生成图片：" + imageResponse.getData().get(0).getUrl());

//        // 编辑图像
//        String url = imageResponse.getData().get(0).getUrl();
//        String tempFileName = "./temp/chatgpt/dall/temp.png";
//        try {
//            BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
//            FileOutputStream fileOutputStream = new FileOutputStream(tempFileName);
//            byte dataBuffer[] = new byte[1024];
//            int bytesRead;
//            while((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
//                fileOutputStream.write(dataBuffer, 0, bytesRead);
//            }
//            System.out.println("文件下载成功");
//        } catch (IOException e) {
//            // 处理异常
//            e.printStackTrace();
//        }
//
//        OpenAiClient client1 = OpenAiClient.builder()
//                .apiKey(Arrays.asList("sk-CMAaXEQfolbfguToC0E89d26626645CfAc8eD3D700CdE266"))
//                .okHttpClient(okHttpClient)
//                .apiHost("https://api.qqslyx.com/")
//                .build();
//        List<Item> images = client1.editImages(new File(tempFileName),
//                "去除图片的背景");
//        System.out.println("编辑图片：" + images);
//
//        // 改变图像
//        ImageVariations imageVariations = ImageVariations.builder().build();
//        ImageResponse imageResponse1 = client1.variationsImages(new java.io.File(tempFileName), imageVariations);
//        System.out.println("改变图片：" + imageResponse1);

        // 6、tools call 阻塞式
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
//                .apiKey(Arrays.asList("sk-GxoeUJ0L8vOjWRSP0eAbD53e4fA8436fB894Aa4c4f9f1220"))
//                .okHttpClient(okHttpClient)
//                .apiHost("https://api.qqslyx.com/")
//                .build();
//        Message message = Message.builder().role(Message.Role.USER).content("给我输出一个长度为3的中文词语，并解释下词语对应物品的用途").build();
//        //属性一
//        JSONObject wordLength = new JSONObject();
//        wordLength.putOpt("type", "number");
//        wordLength.putOpt("description", "词语的长度");
//        //属性二
//        JSONObject language = new JSONObject();
//        language.putOpt("type", "string");
//        language.putOpt("enum", Arrays.asList("zh", "en"));
//        language.putOpt("description", "语言类型，例如：zh代表中文、en代表英语");
//        //参数
//        JSONObject properties = new JSONObject();
//        properties.putOpt("wordLength", wordLength);
//        properties.putOpt("language", language);
//        Parameters parameters = Parameters.builder()
//                .type("object")
//                .properties(properties)
//                .required(Collections.singletonList("wordLength")).build();
//        Tools tools = Tools.builder()
//                .type(Tools.Type.FUNCTION.getName())
//                .function(ToolsFunction.builder().name("getOneWord").description("获取一个指定长度和语言类型的词语").parameters(parameters).build())
//                .build();
//
//        ChatCompletion chatCompletion = ChatCompletion
//                .builder()
//                .messages(Collections.singletonList(message))
//                .tools(Collections.singletonList(tools))
//                .model(ChatCompletion.Model.GPT_4_1106_PREVIEW.getName())
//                .build();
//        ChatCompletionResponse chatCompletionResponse = client.chatCompletion(chatCompletion);
//
//        ChatChoice chatChoice = chatCompletionResponse.getChoices().get(0);
//        log.info("构造的方法值：{}", chatChoice.getMessage());
//        // 返回结果不一定能成功调用tools，要么chatChoice.getMessage().getToolCalls()为空，要么chatChoice.getMessage().getContent()为空，二选一
//        log.info("构造的方法值：{}", chatChoice.getMessage().getContent());
//        log.info("构造的方法值：{}", chatChoice.getMessage().getToolCalls());
//        if (chatChoice.getMessage().getToolCalls().size() > 0) {
//            ToolCalls openAiReturnToolCalls = chatChoice.getMessage().getToolCalls().get(0);
//            WordParam wordParam = JSONUtil.toBean(openAiReturnToolCalls.getFunction().getArguments(), WordParam.class);
//            String oneWord = getOneWord(wordParam);
//
//            ToolCallFunction tcf = ToolCallFunction.builder().name("getOneWord").arguments(openAiReturnToolCalls.getFunction().getArguments()).build();
//            ToolCalls tc = ToolCalls.builder().id(openAiReturnToolCalls.getId()).type(ToolCalls.Type.FUNCTION.getName()).function(tcf).build();
//            //构造tool call
//            Message message2 = Message.builder().role(Message.Role.ASSISTANT).content("方法参数").toolCalls(Collections.singletonList(tc)).build();
//            String content
//                    = "{ " +
//                    "\"wordLength\": \"3\", " +
//                    "\"language\": \"zh\", " +
//                    "\"word\": \"" + oneWord + "\"," +
//                    "\"用途\": [\"直接吃\", \"做沙拉\", \"售卖\"]" +
//                    "}";
//            Message message3 = Message.builder().toolCallId(openAiReturnToolCalls.getId()).role(Message.Role.TOOL).name("getOneWord").content(content).build();
//            List<Message> messageList = Arrays.asList(message, message2, message3);
//            ChatCompletion chatCompletionV2 = ChatCompletion
//                    .builder()
//                    .messages(messageList)
//                    .model(ChatCompletion.Model.GPT_4_1106_PREVIEW.getName())
//                    .build();
//            ChatCompletionResponse chatCompletionResponseV2 = client.chatCompletion(chatCompletionV2);
//            log.info("自定义的方法返回值：{}", chatCompletionResponseV2.getChoices().get(0).getMessage().getContent());
//        }


        // 7、tools call：流式调用
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
//        OpenAiStreamClient streamClient = OpenAiStreamClient.builder()
//                // apiKey
//                .apiKey(Arrays.asList("sk-CMAaXEQfolbfguToC0E89d26626645CfAc8eD3D700CdE266"))
//                // 自定义Key的获取策略：默认KeyRandomStrategy
//                .keyStrategy(new KeyRandomStrategy())
//                .authInterceptor(new DynamicKeyOpenAiAuthInterceptor())
//                .okHttpClient(okHttpClient)
//                // 自己做了代理就传代理地址，没有可不传
//                .apiHost("https://api.qqslyx.com/")
//                .build();
//
//        CountDownLatch countDownLatch = new CountDownLatch(1);
//        ToolCallSSEEventSourceListener eventSourceListener = new ToolCallSSEEventSourceListener(countDownLatch);
//
//        Message message = Message.builder().role(Message.Role.USER).content("给我输出一个长度为3的中文词语，并解释下词语对应物品的用途").build();
//        //属性一
//        JSONObject wordLength = new JSONObject();
//        wordLength.putOpt("type", "number");
//        wordLength.putOpt("description", "词语的长度");
//        //属性二
//        JSONObject language = new JSONObject();
//        language.putOpt("type", "string");
//        language.putOpt("enum", Arrays.asList("zh", "en"));
//        language.putOpt("description", "语言类型，例如：zh代表中文、en代表英语");
//        //参数
//        JSONObject properties = new JSONObject();
//        properties.putOpt("wordLength", wordLength);
//        properties.putOpt("language", language);
//        Parameters parameters = Parameters.builder()
//                .type("object")
//                .properties(properties)
//                .required(Collections.singletonList("wordLength")).build();
//        Tools tools = Tools.builder()
//                .type(Tools.Type.FUNCTION.getName())
//                .function(ToolsFunction.builder().name("getOneWord").description("获取一个指定长度和语言类型的词语").parameters(parameters).build())
//                .build();
//
//        ChatCompletion chatCompletion = ChatCompletion
//                .builder()
//                .messages(Collections.singletonList(message))
//                .tools(Collections.singletonList(tools))
//                .model(ChatCompletion.Model.GPT_3_5_TURBO_1106.getName())
//                .build();
//        streamClient.streamChatCompletion(chatCompletion, eventSourceListener);
//
//        try {
//            countDownLatch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        ToolCalls openAiReturnToolCalls = eventSourceListener.getToolCalls();
//        System.out.println("toolcall:" + openAiReturnToolCalls.toString());
//        if (openAiReturnToolCalls.getId() != null) {
//            WordParam wordParam = JSONUtil.toBean(openAiReturnToolCalls.getFunction().getArguments(), WordParam.class);
//            String oneWord = getOneWord(wordParam);
//
//            ToolCallFunction tcf = ToolCallFunction.builder().name("getOneWord").arguments(openAiReturnToolCalls.getFunction().getArguments()).build();
//            ToolCalls tc = ToolCalls.builder().id(openAiReturnToolCalls.getId()).type(ToolCalls.Type.FUNCTION.getName()).function(tcf).build();
//            //构造tool call
//            Message message2 = Message.builder().role(Message.Role.ASSISTANT).content("方法参数").toolCalls(Collections.singletonList(tc)).build();
//            String content
//                    = "{ " +
//                    "\"wordLength\": \"3\", " +
//                    "\"language\": \"zh\", " +
//                    "\"word\": \"" + oneWord + "\"," +
//                    "\"用途\": [\"直接吃\", \"做沙拉\", \"售卖\"]" +
//                    "}";
//            Message message3 = Message.builder().toolCallId(openAiReturnToolCalls.getId()).role(Message.Role.TOOL).name("getOneWord").content(content).build();
//            List<Message> messageList = Arrays.asList(message, message2, message3);
//            ChatCompletion chatCompletionV2 = ChatCompletion
//                    .builder()
//                    .messages(messageList)
//                    .model(ChatCompletion.Model.GPT_3_5_TURBO_1106.getName())
//                    .build();
//
//            CountDownLatch countDownLatch1 = new CountDownLatch(1);
//            streamClient.streamChatCompletion(chatCompletionV2, new ToolCallSSEEventSourceListener(countDownLatch1));
//            try {
//                countDownLatch1.await();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }


    }

    @Data
    @Builder
    static class WordParam {
        private int wordLength;
        @Builder.Default
        private String language = "zh";
    }

    /**
     * 获取一个词语
     *
     * @param wordParam
     * @return
     */
    public static String getOneWord(WordParam wordParam) {
        List<String> zh = Arrays.asList("大香蕉", "哈密瓜", "苹果");
        List<String> en = Arrays.asList("apple", "banana", "cantaloupe");
        if (wordParam.getLanguage().equals("zh")) {
            for (String e : zh) {
                if (e.length() == wordParam.getWordLength()) {
                    return e;
                }
            }
        }
        if (wordParam.getLanguage().equals("en")) {
            for (String e : en) {
                if (e.length() == wordParam.getWordLength()) {
                    return e;
                }
            }
        }
        return "西瓜";
    }
}
