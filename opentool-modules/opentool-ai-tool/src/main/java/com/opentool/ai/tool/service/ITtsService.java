package com.opentool.ai.tool.service;

import com.opentool.ai.tool.domain.vo.TtsRequest;

import java.util.List;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/10/31 15:18
 */
public interface ITtsService {
    List<String> getLanguages();

    List<String> getRolesByLanguageAndGender(String language, String gender);

    String synthesizeVoice(TtsRequest ttsRequest) throws Exception;
}
