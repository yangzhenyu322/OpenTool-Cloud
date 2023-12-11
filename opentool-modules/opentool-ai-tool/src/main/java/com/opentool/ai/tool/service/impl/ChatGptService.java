package com.opentool.ai.tool.service.impl;

import com.opentool.ai.tool.domain.vo.ChatRequest;
import com.opentool.ai.tool.domain.vo.DialogContent;
import com.opentool.ai.tool.service.IChatGptService;
import com.opentool.ai.tool.service.strategy.IChatGptStrategy;
import com.opentool.system.api.RemoteFileService;
import com.opentool.system.api.RemoteImageConvertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * ChatGPT工厂服务类
 * / @Author: ZenSheep
 * / @Date: 2023/12/8 22:04
 */
@Service
public class ChatGptService implements IChatGptService {
    /**
     * Spring 会自动将Strategy接口的实现类注入到这个Map中，Key为Bean Id，value为对应的策略实现类
     */
    @Autowired
    private Map<String, IChatGptStrategy> chatGPTStrategyMap;

    @Autowired
    private RemoteFileService remoteFileService;
    @Autowired
    RemoteImageConvertService remoteImageConvertService;

    @Override
    public String sseChat(ChatRequest chatRequest, String chatGPTStrategyType) {
        IChatGptStrategy targetChatGPTStrategy = Optional.ofNullable(chatGPTStrategyMap.get(chatGPTStrategyType))
                .orElseThrow(() -> new IllegalArgumentException(("非法模型")));
        return targetChatGPTStrategy.sseChat(chatRequest);
    }

    @Override
    public List<DialogContent> getHistoryList(String uid, String wid, String chatGPTStrategyType) {
        IChatGptStrategy targetChatGPTStrategy = Optional.ofNullable(chatGPTStrategyMap.get(chatGPTStrategyType))
                .orElseThrow(() -> new IllegalArgumentException(("非法模型")));
        return targetChatGPTStrategy.getHistoryList(uid, wid);
    }

    @Override
    public String cleanHistoryLog(String uid, String wid, String chatGPTStrategyType) {
        IChatGptStrategy targetChatGPTStrategy = Optional.ofNullable(chatGPTStrategyMap.get(chatGPTStrategyType))
                .orElseThrow(() -> new IllegalArgumentException(("非法模型")));
        return targetChatGPTStrategy.cleanHistoryLog(uid, wid) > 0 ? "清除历史对话成功" : "清除历史对话失败，对话不存在";
    }

    @Override
    public Map<String, String> uploadFile(MultipartFile file, int width, int height) {
        Map<String, String> urlMap = new HashMap<>();
        // 原始图片url
        String url = remoteFileService.uploadFile(file, "ChatGpt/image");
//        String convertUrl = remoteImageConvertService.convertSize(Arrays.asList(url), width, height).get(0);
        urlMap.put("url", url);
        // 处理后的图片url
//        urlMap.put("convertUrl", convertUrl);
        return urlMap;
    }
}