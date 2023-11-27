package com.opentool.ai.tool.service.impl;

import cn.hutool.core.util.StrUtil;
import com.microsoft.cognitiveservices.speech.CancellationReason;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.audio.*;
import com.microsoft.cognitiveservices.speech.transcription.ConversationTranscriber;
import com.opentool.ai.tool.cache.SseLocalCache;
import com.opentool.ai.tool.domain.entity.SttLanguage;
import com.opentool.ai.tool.mapper.SttLanguageMapper;
import com.opentool.ai.tool.service.ISttService;
import com.opentool.ai.tool.utils.BinaryAudioStreamReader;
import com.opentool.common.core.exception.base.BaseException;
import com.opentool.common.core.utils.date.DateUtils;
import com.opentool.system.api.RemoteFileService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/11/20 14:30
 */
@Slf4j
@Service
public class SttService implements ISttService {
    @Value("${stt.apiKey}")
    private String apiKey;
    @Value("${stt.region}")
    private String region;

    @Autowired
    private RemoteFileService remoteFileService;
    @Autowired
    private SttLanguageMapper sttLanguageMapper;

    @Override
    public String uploadFile(MultipartFile file, String storagePath) {
        return remoteFileService.uploadFile(file, storagePath);
    }

    @Override
    public String speechRecognition(String uid, String urlPath, String targetLanguage) throws IOException, ExecutionException, InterruptedException {
        if (StrUtil.isBlank(urlPath)) {
            log.error("[{}]url路径为空，无法进行音频识别", uid);
            throw new BaseException("参数异常，url路径不能为空");
        }

        Thread sttThread = new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                String url = urlPath; // 目标音频的url路径
                SseEmitter sseEmitter = (SseEmitter) SseLocalCache.CACHE.get(uid);
                if (sseEmitter == null) {
                    log.info("[{}]获取sse失败,没有创建连接，请重试。", uid);
                    throw new com.unfbx.chatgpt.exception.BaseException("[{}]获取sse失败,没有创建连接，请重试~");
                }

                SpeechConfig speechConfig = SpeechConfig.fromSubscription(apiKey, region);
                // 识别语句的偏移量和持续时间（recognized）
                speechConfig.requestWordLevelTimestamps();
                // 支持的语言(如果设置多语言识别会导致缺少transcribing事件)
//                AutoDetectSourceLanguageConfig autoDetectSourceLanguageConfig = AutoDetectSourceLanguageConfig.fromLanguages(Arrays.asList("en-US", "zh-CN", "ja-JP"));
                // GStreamer:支持音频文件的不同格式
                PullAudioInputStream pullAudioInputStream = AudioInputStream.createPullStream(new BinaryAudioStreamReader(new URL(urlPath)),
                        AudioStreamFormat.getCompressedFormat(AudioStreamContainerFormat.ANY));
                AudioConfig audioConfig = AudioConfig.fromStreamInput(pullAudioInputStream);
                speechConfig.setSpeechRecognitionLanguage(targetLanguage);
                // 初始化信号量
                Semaphore stopRecognitionSemaphore = new Semaphore(0);
                // ConversationTranscriber配置
//                ConversationTranscriber conversationTranscriber = new ConversationTranscriber(speechConfig, autoDetectSourceLanguageConfig, audioConfig);
                ConversationTranscriber conversationTranscriber = new ConversationTranscriber(speechConfig, audioConfig);
                {
                    // 连续语音识别配置
                    // transcribing：事件信号，包含中间识别结果
                    conversationTranscriber.transcribing.addEventListener((s, e) -> {
                        // 识别偏移量和持续时间: 一个时钟周期表示100纳秒，即一千万分之一秒
                        BigInteger offset =  e.getResult().getOffset().multiply(BigInteger.valueOf(100));
                        BigInteger duration = e.getResult().getDuration().multiply(BigInteger.valueOf(100));
                        String beginTimeStamp = DateUtils.convertNanoTimestamp(offset);
                        String endTimeStamp = DateUtils.convertNanoTimestamp(duration);
                        log.info("[transcribing]-【" + beginTimeStamp + "," + endTimeStamp + "】:" + e.getResult().getText());

                        // 向用户传输消息
                        Map<String, Object> sseDataMap = new HashMap<>();
                        sseDataMap.put("textContent", e.getResult().getText());
                        try {
                            sseEmitter.send(SseEmitter.event()
                                    .id("[transcribing]")
                                    .data(sseDataMap)
                                    .reconnectTime(3000));
                        } catch (Exception ex) {
                            log.error("[transcribing]sse信息推送失败：" + ex);
                            // 结束本次语音识别，转到sessionStopped
                            stopRecognitionSemaphore.release();

                            try {
                                // 等待本次语音识别关闭会话
                                Thread.sleep(1000);
                            } catch (InterruptedException exc) {
                                throw new RuntimeException(exc);
                            }
                        }
                    });

                    // transcribed：包含最终识别结果的时间信号，指示成功的识别尝试
                    conversationTranscriber.transcribed.addEventListener((s, e) -> {
                        if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
                            log.info("[transcribed]语音识别结果：");
                            // 识别语句后，即可获取已识别语音的偏移量和持续时间
                            // 一个时钟周期表示一百纳秒，即一千万分之一秒
                            BigInteger offset =  e.getResult().getOffset().multiply(BigInteger.valueOf(100));
                            BigInteger duration = e.getResult().getDuration().multiply(BigInteger.valueOf(100));
                            String beginTimeStamp = DateUtils.convertNanoTimestamp(offset);
                            String endTimeStamp = DateUtils.convertNanoTimestamp(offset.add(duration));
                            // 获取当前说话人ID
                            // 备注：该服务在单个说话人提供至少 7 秒的连续音频时性能最佳。 这使系统能够正确地区分说话人。 否则，说话人 ID 将返回为 Unknown。
                            String speakerId = e.getResult().getSpeakerId();
                            // 输出识别结果（一句话）
                            log.info("【" + beginTimeStamp + "," + endTimeStamp + "】" +  speakerId + ":" + e.getResult().getText());

                            // 向用户传输消息
                            Map<String, Object> sseDataMap = new HashMap<>();
                            sseDataMap.put("timestamp", "[" + beginTimeStamp + "," + endTimeStamp + "]");
                            sseDataMap.put("role", speakerId);
                            sseDataMap.put("textContent", e.getResult().getText());
                            try {
                                sseEmitter.send(SseEmitter.event()
                                        .id("[transcribed]")
                                        .data(sseDataMap)
                                        .reconnectTime(3000));
                            } catch (Exception ex) {
                                log.error("[transcribed]sse信息推送失败：" + ex);
                                // 结束本次语音识别，转到sessionStopped
                                stopRecognitionSemaphore.release();
                            }
                        } else if (e.getResult().getReason() == ResultReason.NoMatch) {
                            log.warn("无法匹配：语音不能被识别");
                        }
                    });

                    // canceled：时间信号，包含已取消的识别结果。这些结果指示因直接取消请求而取消的识别尝试。或者，他们指示传输协议失败。
                    conversationTranscriber.canceled.addEventListener((s, e) -> {
                        log.info("[canceled]取消：Reason=" + e.getReason());
                        // 结束消息推送
                        try {
                            Map<String, Object> sseDataMap = new HashMap<>();
                            sseDataMap.put("canceled", "完成对话");
                            sseEmitter.send(SseEmitter.event()
                                    .id("[canceled]")
                                    .data(sseDataMap)
                                    .reconnectTime(3000));
                        } catch (IOException ex) {
                            log.error("[canceled]sse信息推送失败：" + ex);
                        }

                        if (e.getReason() == CancellationReason.Error) {
                            log.error("[canceled]取消:ErrorCode=" + e.getErrorCode());
                            log.error("[canceled]取消:ErrorDetails=" + e.getErrorDetails());
                            log.error("[canceled]取消:Did you set the speech resource key and region values?");
                        }
                        // 结束本次语音识别，转到sessionStopped
                        stopRecognitionSemaphore.release();
                        // 传输完成后自动关闭sse
                        sseEmitter.complete();
                    });

                    // sessionStarted: 事件信号，指示识别会话的开始
                    conversationTranscriber.sessionStarted.addEventListener((s, e) -> {
                        log.info("\n    Session started event.");
                    });

                    // sessionStopped：事件信号，指示识别会话的结束
                    conversationTranscriber.sessionStopped.addEventListener((s, e) -> {
                        log.info("\n    Session stopped event.");
                    });
                }

                // 设置所有项后，开始识别
                conversationTranscriber.startTranscribingAsync().get();
                stopRecognitionSemaphore.acquire();
                conversationTranscriber.stopTranscribingAsync().get();

                conversationTranscriber.close();
                audioConfig.close();
                speechConfig.close();
            }
        });
        // 启动stt线程
        sttThread.start();

        return "[" + uid + "]开始进行音频识别";
    }

    @Override
    public List<SttLanguage> getLanguages() {
        return sttLanguageMapper.selectList(null);
    }
}
