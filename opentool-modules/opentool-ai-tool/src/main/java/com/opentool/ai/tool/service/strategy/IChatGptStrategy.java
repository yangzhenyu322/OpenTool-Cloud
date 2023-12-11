package com.opentool.ai.tool.service.strategy;

import com.opentool.ai.tool.domain.vo.ChatRequest;
import com.opentool.ai.tool.domain.vo.DialogContent;

import java.util.List;

/** ChatGPT策略接口
 * / @Author: ZenSheep
 * / @Date: 2023/10/18 20:24
 */
public interface IChatGptStrategy {
    String sseChat(ChatRequest chatRequest);

    List<DialogContent> getHistoryList(String uid, String wid);

    int cleanHistoryLog(String uid, String wid);
}
