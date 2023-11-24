package com.opentool.ai.tool.service;

import java.util.List;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/10/18 20:24
 */
public interface IChatGPTService {
    Long sseChat(String uid, String wid, String msg);

    List<String> getHistoryList(String uid, String wid);

    int cleanHistoryLog(String uid, String wid);
}
