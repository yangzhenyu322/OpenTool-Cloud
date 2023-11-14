package com.opentool.ai.tool.controller;

import com.opentool.ai.tool.domain.vo.TtsRequest;
import com.opentool.ai.tool.service.ITtsService;
import com.opentool.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/10/31 15:16
 */
@RefreshScope
@RestController
@RequestMapping("/tts")
public class TtsController {
    @Autowired
    private ITtsService ttsService;

    /**
     * 获取语言列表
     * @return
     */
    @GetMapping("/languages")
    public R<?> getTtsLanguages() {
        List<String> languages = ttsService.getLanguages();
        return R.ok(languages);
    }

    /**
     * 获取特定语言的所有角色
     * @param language
     * @return
     */
    @GetMapping("/roles/{language}/{gender}")
    public R<?> getTtsRolesByLanguageAndSex(@PathVariable("language") String language, @PathVariable("gender") String gender) {
        List<String> ttsRoles = ttsService.getRolesByLanguageAndGender(language, gender);
        return R.ok(ttsRoles);
    }

    /**
     * 文字转声音接口
     * @param ttsRequest
     * @return
     * @throws Exception
     */
    @PostMapping("/voice")
    public R<?> getTtsVoice(@RequestBody TtsRequest ttsRequest) throws Exception {
        String outputFileName = ttsService.synthesizeVoice(ttsRequest);
        return R.ok(outputFileName);
    }
}