package com.opentool.ai.tool.service;

import com.opentool.ai.tool.domain.entity.SttLanguage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/11/20 14:30
 */
public interface ISttService {
    String uploadFile(MultipartFile file, String storagePath);

    String speechRecognition(String uid, String urlPath, String targetLanguage) throws IOException, ExecutionException, InterruptedException;

    List<SttLanguage> getLanguages();
}
