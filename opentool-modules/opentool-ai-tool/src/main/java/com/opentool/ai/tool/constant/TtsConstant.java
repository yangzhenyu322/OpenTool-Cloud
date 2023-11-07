package com.opentool.ai.tool.constant;

import com.opentool.ai.tool.utils.AudioFormatUtils;

/**
 * TTS服务常量
 * / @Author: ZenSheep
 * / @Date: 2023/10/30 20:35
 */
public class TtsConstant {
    // TTS API KEY
    public static  final String API_KEY = "dbc728ec904c412db821e49484047059";
    // token授权Uri
    public static final String ACCESS_TOKEN_URI = "https://eastasia.api.cognitive.microsoft.com/sts/v1.0/issueToken";
    // TTS服务Uri
    public static final String TTS_SERVER_URI = "https://eastasia.tts.speech.microsoft.com/cognitiveservices/v1";
    // 语言：zh-CN、en_US
    public static final String LOCALE = "yue-CN";
    // 性别：Male、Female
    public static final String GENDER = "Female";
    // 角色
    public static final String SHORTNAME = "yue-CN-XiaoMinNeural";
    // 合成语音格式
    public static final String AUDIO_FORMAT = AudioFormatUtils.AUDIO_24KHZ_48KBITRATE_MONO_MP3;
    // 文件本地缓存路径
    public static final String FILE_TEMP_PATH = "./temp/tts/voice/temp.mp3";
    // 文件OSS上传路径
    public static final String OSS_UPLOAD_PATH = "tts/voice";
}
