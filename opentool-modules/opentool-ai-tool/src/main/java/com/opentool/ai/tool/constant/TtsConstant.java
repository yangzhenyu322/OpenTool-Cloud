package com.opentool.ai.tool.constant;

/**
 * TTS服务常量
 * / @Author: ZenSheep
 * / @Date: 2023/10/30 20:35
 */
public class TtsConstant {
    // token授权Uri
    public static final String ACCESS_TOKEN_URI = "https://eastasia.api.cognitive.microsoft.com/sts/v1.0/issueToken";
    // TTS服务Uri
    public static final String TTS_SERVER_URI = "https://eastasia.tts.speech.microsoft.com/cognitiveservices/v1";
    // 文件OSS上传路径
    public static final String OSS_UPLOAD_PATH = "tts/voice";
}
