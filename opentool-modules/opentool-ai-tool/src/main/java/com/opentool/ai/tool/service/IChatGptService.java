package com.opentool.ai.tool.service;

import com.opentool.ai.tool.domain.vo.ChatRequest;
import com.opentool.ai.tool.domain.vo.DialogContent;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/12/11 15:19
 */
public interface IChatGptService {
    String sseChat(ChatRequest chatRequest, String strategyType);

    List<DialogContent> getHistoryList(String uid, String wid, String strategyType);

    String cleanHistoryLog(String uid, String wid, String strategyType);

    Map<String, String> uploadFile(MultipartFile file, int width, int height);
}
