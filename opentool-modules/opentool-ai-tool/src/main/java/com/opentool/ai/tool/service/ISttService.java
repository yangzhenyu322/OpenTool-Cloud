package com.opentool.ai.tool.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/11/20 14:30
 */
public interface ISttService {
    String uploadFile(MultipartFile file, String storagePath);

    String speechRecognition(String uid, String urlPath) throws IOException, ExecutionException, InterruptedException;
}
