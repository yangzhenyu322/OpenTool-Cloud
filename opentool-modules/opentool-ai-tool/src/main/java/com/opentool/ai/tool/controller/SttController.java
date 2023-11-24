package com.opentool.ai.tool.controller;

import com.opentool.ai.tool.domain.vo.SttRequest;
import com.opentool.ai.tool.service.ISseService;
import com.opentool.ai.tool.service.ISttService;
import com.opentool.common.core.domain.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/11/20 14:21
 */
@Slf4j
@RefreshScope
@RestController
@RequestMapping("/stt")
public class SttController {
    @Autowired
    private ISttService sttService;
    @Autowired
    private ISseService sseService;

    /**
     * 音频文件上传接口
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<?> uploadFile(@RequestPart("file")MultipartFile file) {
        return R.ok(sttService.uploadFile(file, "SpeechRecognition/audio"));
    }

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

    @PostMapping("/speechRecognition")
    public R<?> speechRecognition(@RequestBody SttRequest sttRequest) throws IOException, ExecutionException, InterruptedException {
        log.info("[{}]请求转换音频：[{}]",sttRequest.getUid(), sttRequest.getUrlPath());
        return R.ok(sttService.speechRecognition(sttRequest.getUid(), sttRequest.getUrlPath()));
    }
}