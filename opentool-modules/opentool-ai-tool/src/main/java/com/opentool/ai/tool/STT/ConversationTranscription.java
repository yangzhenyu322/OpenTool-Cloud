package com.opentool.ai.tool.STT;

import com.microsoft.cognitiveservices.speech.AutoDetectSourceLanguageConfig;
import com.microsoft.cognitiveservices.speech.CancellationReason;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.audio.*;
import com.microsoft.cognitiveservices.speech.transcription.ConversationTranscriber;
import com.opentool.ai.tool.utils.BinaryAudioStreamReader;
import com.opentool.common.core.utils.date.DateUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

// 多语言设置：https://learn.microsoft.com/zh-cn/azure/ai-services/speech-service/language-identification?tabs=continuous&pivots=programming-language-java
// 使用短语列表提高识别能力： https://learn.microsoft.com/zh-cn/azure/ai-services/speech-service/improve-accuracy-phrase-list?tabs=terminal&pivots=programming-language-java
// 显示文本格式设置：https://learn.microsoft.com/zh-cn/azure/ai-services/speech-service/display-text-format?pivots=programming-language-java
// 语言支持：https://learn.microsoft.com/zh-cn/azure/ai-services/speech-service/language-support?tabs=stt
// 应用场景：https://learn.microsoft.com/zh-cn/azure/ai-services/speech-service/whisper-overview

/**
 * 对话转录：该服务在单个说话人提供至少 7 秒的连续音频时性能最佳。 这使系统能够正确地区分说话人。 否则，说话人 ID 将返回为 Unknown。
 * 参考：https://learn.microsoft.com/zh-cn/azure/ai-services/speech-service/get-started-stt-diarization?tabs=windows&pivots=programming-language-java
 * / @Author: ZenSheep
 * / @Date: 2023/11/17 20:41
 */
public class ConversationTranscription {
    // This example requires environment variables named "SPEECH_KEY" and "SPEECH_REGION"
    private static String speechKey = "f258c08822184feea388f6b58160295e";
    private static String speechRegion = "eastasia";
    private static String filePath = "./temp/stt/voice/conversation/two/汪苏泷&BY2《有点甜》.mp3";
    private static String urlPath ="https://opentool.oss-cn-shenzhen.aliyuncs.com/SpeechRecognition/audio/c65f2ffd-ea7f-4ce6-8f3c-0d4a8bab9a70/%E6%B1%AA%E8%8B%8F%E6%B3%B7%26BY2%E3%80%8A%E6%9C%89%E7%82%B9%E7%94%9C%E3%80%8B.mp3";

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        Thread sttThread = new Thread(new Runnable() {
            @Override
            public void run() {
                SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey, speechRegion);
//        speechConfig.setSpeechRecognitionLanguage("en-US");
                // 识别语句的偏移量和持续时间（recognized）
                speechConfig.requestWordLevelTimestamps();
                // 支持的语言
                AutoDetectSourceLanguageConfig autoDetectSourceLanguageConfig = AutoDetectSourceLanguageConfig.fromLanguages(Arrays.asList("en-US", "zh-CN", "de-DE"));

                // GStreamer
                PullAudioInputStream pullAudioInputStream = null;
                try {
                    pullAudioInputStream = AudioInputStream.createPullStream(new BinaryAudioStreamReader(new URL(urlPath)),
                            AudioStreamFormat.getCompressedFormat(AudioStreamContainerFormat.ANY));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                AudioConfig audioConfig = AudioConfig.fromStreamInput(pullAudioInputStream);

                // 1.初始化信号量
                Semaphore stopRecognitionSemaphore = new Semaphore(0);
                ConversationTranscriber conversationTranscriber = new ConversationTranscriber(speechConfig, autoDetectSourceLanguageConfig, audioConfig);
                {
                    // 2.连续语音识别配置
                    // transcribing：事件信号，包含中间识别结果
                    conversationTranscriber.transcribing.addEventListener((s, e) -> {
                        // 识别偏移量和持续时间: 一个时钟周期表示100纳秒，即一千万分之一秒
                        BigInteger offset =  e.getResult().getOffset().multiply(BigInteger.valueOf(100));
                        BigInteger duration = e.getResult().getDuration().multiply(BigInteger.valueOf(100));

                        String beginTimeStamp = DateUtils.convertNanoTimestamp(offset);
                        String endTimeStamp = DateUtils.convertNanoTimestamp(duration);
                        System.out.println("[transcribing]-【" + beginTimeStamp + "," + endTimeStamp + "】:" + e.getResult().getText());
                    });

                    // transcribed：包含最终识别结果的时间信号，指示成功的识别尝试
                    conversationTranscriber.transcribed.addEventListener((s, e) -> {
                        if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
                            System.out.println("[transcribed]语音识别结果：");
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
                            System.out.println("【" + beginTimeStamp + "," + endTimeStamp + "】" +  speakerId + ":" + e.getResult().getText());
                        } else if (e.getResult().getReason() == ResultReason.NoMatch) {
                            System.out.println("无法匹配：语音不能被识别");
                        }
                    });

                    // canceled：时间信号，包含已取消的识别结果。这些结果指示因直接取消请求而取消的识别尝试。或者，他们指示传输协议失败。
                    conversationTranscriber.canceled.addEventListener((s, e) -> {
                        System.out.println("[canceled]取消：Reason=" + e.getReason());

                        if (e.getReason() == CancellationReason.Error) {
                            System.out.println("[canceled]取消:ErrorCode=" + e.getErrorCode());
                            System.out.println("[canceled]取消:ErrorDetails=" + e.getErrorDetails());
                            System.out.println("[canceled]取消:Did you set the speech resource key and region values?");
                        }

                        stopRecognitionSemaphore.release();
                    });

                    // sessionStarted: 事件信号，指示识别会话的开始
                    conversationTranscriber.sessionStarted.addEventListener((s, e) -> {
                        System.out.println("\n    Session started event.");
                    });

                    // sessionStopped：事件信号，指示识别会话的结束
                    conversationTranscriber.sessionStopped.addEventListener((s, e) -> {
                        System.out.println("\n    Session stopped event.");
                    });
                }

                // 3.设置所有项后，开始识别
                try {
                    conversationTranscriber.startTranscribingAsync().get();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }

                try {
                    stopRecognitionSemaphore.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                try {
                    conversationTranscriber.stopTranscribingAsync().get();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }

                conversationTranscriber.close();
                audioConfig.close();
                speechConfig.close();
            }
        });

        // 启动stt线程
        sttThread.start();
    }
}