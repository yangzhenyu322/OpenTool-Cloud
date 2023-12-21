package com.opentool.ai.tool.service.strategy.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.opentool.ai.tool.cache.SseLocalCache;
import com.opentool.ai.tool.domain.entity.ChatLog;
import com.opentool.ai.tool.domain.vo.ChatRequest;
import com.opentool.ai.tool.domain.vo.DialogContent;
import com.opentool.ai.tool.listener.ToolCallSSEEventSourceListener;
import com.opentool.ai.tool.mapper.ChatLogMapper;
import com.opentool.ai.tool.service.strategy.IChatGptStrategy;
import com.opentool.common.core.utils.file.FileUtils;
import com.opentool.common.core.utils.file.MultipartFileUtils;
import com.opentool.system.api.RemoteFileService;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.*;
import com.unfbx.chatgpt.entity.chat.tool.ToolCallFunction;
import com.unfbx.chatgpt.entity.chat.tool.ToolCalls;
import com.unfbx.chatgpt.entity.chat.tool.Tools;
import com.unfbx.chatgpt.entity.chat.tool.ToolsFunction;
import com.unfbx.chatgpt.entity.images.Image;
import com.unfbx.chatgpt.entity.images.ImageResponse;
import com.unfbx.chatgpt.entity.images.ResponseFormat;
import com.unfbx.chatgpt.entity.images.SizeEnum;
import com.unfbx.chatgpt.exception.BaseException;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.File;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** dall图像生成策略实现类
 * / @Author: ZenSheep
 * / @Date: 2023/12/14 22:04
 */
@Slf4j
@Service
public class ToolCallDallGpt implements IChatGptStrategy {
    @Autowired
    private OpenAiStreamClient openAiStreamClient3_5; // 流式对话：用于与用户chat
    @Autowired
    private OpenAiClient openAiClient3_5; // 阻塞对话：用于总结历史对话
    @Autowired
    private ChatLogMapper chatLogMapper;
    @Value("${chatgpt.summary.rule}")
    private String rule;

    @Autowired
    private RemoteFileService remoteFileService;

    @SneakyThrows
    @Override
    public String sseChat(ChatRequest chatRequest) {
        if (StrUtil.isBlank(chatRequest.getQuestion())) {
            log.error("[{}]参数异常，msg为null", chatRequest.getUid());
            throw new BaseException("参数异常，msg不能为空~");
        }

        // 获取上下文、总结文本
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", chatRequest.getUid());
        map.put("window_id", chatRequest.getWid());
        List<ChatLog> chatLogs = chatLogMapper.selectByMap(map);
        ChatLog chatLog; // 对话日志
        String messageContext; // 对话存储文本
        String imageUrlsListContext; // 对话图像存储文本
        String summary; // 总结存储文本

        if (chatLogs.size() > 0) {
            chatLog = chatLogs.get(0);
            messageContext = chatLog.getContent();
            imageUrlsListContext = chatLog.getImageUrls();
            summary = chatLog.getSummary();
        } else {
            log.info("[{}]没有历史窗口，开始创建新的对话窗口~", chatRequest.getUid());
            chatLog = new ChatLog();
            chatLog.setUserId(chatRequest.getUid());
            chatLog.setWindowId(chatRequest.getWid());
            chatLog.setCreateTime(new Date());
            chatLog.setContent(null);
            chatLog.setImageUrls(null);
            chatLog.setRule(rule);
            chatLog.setSummary("");
            chatLogMapper.insert(chatLog);
            log.info("新的对话窗口创建成功");

            messageContext = null;
            imageUrlsListContext = null;
            summary = null;
        }

        // 历史对话列表
        List<Message> messages = new ArrayList<>();  // 原始对话
        List<List<String>> imageUrlsList = new ArrayList<>(); // 原始对话图像
        List<Message> chatMessages = new ArrayList<>(); // 总结对话
        if (StrUtil.isNotBlank(messageContext)) {
            messages = JSONUtil.toList(messageContext, Message.class);
            imageUrlsList = convertToListOfLists(imageUrlsListContext);
            // 取出最近5个对话
            if (messages.size() >= 5 * 2) {
                chatMessages = new ArrayList<>(messages.subList(messages.size() - 5 * 2, messages.size()));
                // 联系上下文
                if (StrUtil.isNotBlank(summary)) {
                    chatMessages.add(0, Message.builder().content(summary).role(Message.Role.ASSISTANT).build());
                }

                if (messages.size() % 5 == 0) {
                    // 历史对话为5的倍数，开始进行总结
                    log.info("历史对话为5的倍数，开始进行上下文总结");
                    summary = summaryHistoryMessages(chatMessages);
                    log.info("总结完成，持久化summary");
                    chatLog.setSummary(summary);
                    chatLogMapper.updateById(chatLog);
                }
            } else {
                chatMessages.addAll(messages);
            }
        }

        // 存储用户当前问题
        Message currentMessage = Message.builder().content(chatRequest.getQuestion()).role(Message.Role.USER).build();
        messages.add(currentMessage);
        imageUrlsList.add(new ArrayList<>());
        chatMessages.add(currentMessage);

        SseEmitter sseEmitter = (SseEmitter) SseLocalCache.CACHE.get(chatRequest.getUid());
        if (sseEmitter == null) {
            log.info("[{}]获取sse失败,没有创建连接，请重试。", chatRequest.getUid());
            throw new BaseException("[{}]获取sse失败,没有创建连接，请重试~");
        }

        // chat
        log.info("[{}]成功提问：[{}]",chatRequest.getUid(), chatRequest.getQuestion());
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ToolCallSSEEventSourceListener toolCallSSEEventSourceListener = new ToolCallSSEEventSourceListener(sseEmitter, messages, imageUrlsList, new ArrayList<>(), chatLogMapper, chatLog, countDownLatch);
        // 配置tool
        // 属性
        JSONObject prompt = new JSONObject();
        prompt.putOpt("type", "string");
        prompt.putOpt("description", "图片的描述");
        JSONObject imageName = new JSONObject();
        imageName.putOpt("type", "string");
        imageName.putOpt("description", "图像主题名");
        // 参数
        JSONObject properties = new JSONObject();
        properties.putOpt("prompt", prompt);
        properties.putOpt("imageName", imageName);
        Parameters parameters = Parameters.builder()
                .type("object")
                .properties(properties)
                .required(Arrays.asList("prompt"))
                .build();
        Tools tools = Tools.builder()
                .type(Tools.Type.FUNCTION.getName())
                .function(ToolsFunction.builder().name("genImage").description("获取一段描述希望生成图像的文字，并用一个简短的词语总结图片描述的主题").parameters(parameters).build())
                .build();

        ChatCompletion chatCompletion = ChatCompletion
                .builder()
                .messages(chatMessages)
                .tools(Arrays.asList(tools))
                .model(ChatCompletion.Model.GPT_3_5_TURBO_1106.getName())
                .build();
        openAiStreamClient3_5.streamChatCompletion(chatCompletion, toolCallSSEEventSourceListener);

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ToolCalls openAiReturnToolCalls = toolCallSSEEventSourceListener.getToolCalls();
        if (openAiReturnToolCalls.getId() != null) {
            // 开始tool call
            ImageParam imageParam = JSONUtil.toBean(openAiReturnToolCalls.getFunction().getArguments(), ImageParam.class);
            // 生成图像
            String genImageUrl = genImage(imageParam, chatRequest.getModel(), 1, Image.Quality.HD.getName(), SizeEnum.size_1024.getName(), Image.Style.NATURAL.getName());
            log.info("生成图像Url:" + genImageUrl);
            // url转本地文件
            String tempPathName = FileUtils.urlToFilePath(genImageUrl, imageParam.getImageName() + ".png");
            File tempFile = FileUtils.filePathToFile(tempPathName);
            // oss存储：持久化文件，否则过一段时间openAi会删除该文件导致文件无法找到
            String url = remoteFileService.uploadFile(MultipartFileUtils.fileToMultipartFile(tempFile), "ChatGpt/image");
            log.info("oss存储图像Url:" + url);
            // 删除本地缓存文件
            FileUtils.deleteFile(tempPathName);
            List<String> imageUrls = new ArrayList<>();
            imageUrls.add(url);

            ToolCallFunction tcf = ToolCallFunction.builder().name("genImage").arguments(openAiReturnToolCalls.getFunction().getArguments()).build();
            ToolCalls tc = ToolCalls.builder().id(openAiReturnToolCalls.getId()).type(ToolCalls.Type.FUNCTION.getName()).function(tcf).build();

            Message message = Message.builder().role(BaseMessage.Role.ASSISTANT).content("方法参数").toolCalls(Arrays.asList(tc)).build();
            String content =
                "{\n" +
                "  \"prompt\":" + imageParam.getPrompt() + ",\n" +
                "  \"imgName\":" + imageParam.getImageName() + ",\n" +
                "  \"toolCall Result\":已生成图像, 不需要你再生成,\n" +
                "}";
            Message message1 = Message.builder().toolCallId(openAiReturnToolCalls.getId()).role(BaseMessage.Role.TOOL).name("genImage").content(content).build();
            chatMessages.add(message);
            chatMessages.add(message1);
            ChatCompletion chatCompletion1 = ChatCompletion
                    .builder()
                    .messages(chatMessages)
                    .model(ChatCompletion.Model.GPT_3_5_TURBO_1106.getName())
                    .build();

            CountDownLatch countDownLatch1 = new CountDownLatch(1);
            openAiStreamClient3_5.streamChatCompletion(chatCompletion1, new ToolCallSSEEventSourceListener(sseEmitter, messages, imageUrlsList, imageUrls, chatLogMapper, chatLog, countDownLatch1));
            try {
                countDownLatch1.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return "成功调用open api";
    }

    /**
     * 总结历史消息：防止tokens过长
     * @param chatMessages 需要总结的上下文
     * @return 总结文本
     */
    public String summaryHistoryMessages(List<Message> chatMessages) {
        // 满足要求，开始总结
        String summary;
        chatMessages.add(Message.builder().content(rule).role(Message.Role.USER).build());  // 添加总结规则
        // 进行总结
        ChatCompletion chatCompletion = ChatCompletion
                .builder()
                .messages(chatMessages)
                .model(ChatCompletion.Model.GPT_3_5_TURBO_1106.getName())
                .build();
        // 开始阻塞
        ChatCompletionResponse chatCompletionResponse = null;
        chatCompletionResponse = openAiClient3_5.chatCompletion(chatCompletion);

        List<ChatChoice> choices = chatCompletionResponse.getChoices();
        summary = choices.get(0).getMessage().getContent();
        chatMessages.remove(chatMessages.size() - 1); // 移除rule

        return summary;
    }

    @Data
    @Builder
    static class ImageParam {
        private String prompt;
        private String imageName;
    }

    /**
     * dall3生成图片
     * @return
     */
    public String genImage(ImageParam imageParam, String model, int nums, String quality, String size, String style) {
        Image image = Image.builder()
                .responseFormat(ResponseFormat.URL.getName())
                .model(model)
                .prompt(imageParam.getPrompt())
                .n(nums)
                .quality(quality)
                .size(size)
                .style(style)
                .build();
        ImageResponse imageResponse = openAiClient3_5.genImages(image);
        return imageResponse.getData().get(0).getUrl();
    }

    /**
     * 将字符串转化为List<List<String>>对象
     * @param input 字符串
     * @return List<List<String>>对象
     */
    public static List<List<String>> convertToListOfLists(String input) {
        if (input == null) {
            return new ArrayList<>();
        }

        List<List<String>> listOfLists = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[([^\\[\\]]*)\\]");

        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String inner = matcher.group(1).trim();
            List<String> innerList = new ArrayList<>();

            if (inner.isEmpty()) {
                listOfLists.add(innerList); // 如果[]内为空，则添加空列表
            } else {
                // 如果[]内包含内容，按逗号分隔并添加到列表中
                String[] elements = inner.split(",\\s*");
                for (String element : elements) {
                    innerList.add(element);
                }
                listOfLists.add(innerList);
            }
        }

        return listOfLists;
    }

    @Override
    public List<DialogContent> getHistoryList(String uid, String wid) {
        List<DialogContent> dialogContentList = new ArrayList<>();

        Map<String, Object> map = new HashMap<>();
        map.put("user_id", uid);
        map.put("window_id", wid);
        List<ChatLog> chatLogs = chatLogMapper.selectByMap(map);
        if (chatLogs.size() > 0) {
            ChatLog chatLog = chatLogs.get(0);
            List<Message> messages = JSONUtil.toList(chatLog.getContent(), Message.class);
            List<List<String>> imageUrlList = convertToListOfLists(chatLog.getImageUrls());
            // 将List<Message> + List<List<String>> 转换为List<DialogContent>
            for (int i = 0; i < messages.size(); i++) {
                DialogContent dialogContent = new DialogContent();
                // 文本
                dialogContent.setText(messages.get(i).getContent());
                // 图像
                dialogContent.setImageUrlList(imageUrlList.get(i));
                dialogContentList.add(dialogContent);
            }
            return dialogContentList;
        }

        return null;
    }

    @Override
    public int cleanHistoryLog(String uid, String wid) {
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", uid);
        map.put("window_id", wid);
        int result = chatLogMapper.deleteByMap(map);
        log.info(result > 0 ? "清除历史窗口成功":"清除失败，历史窗口不存在");
        return result;
    }
}
