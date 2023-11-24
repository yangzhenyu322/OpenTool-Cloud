package com.opentool.ai.tool.STT;

import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.*;
import com.microsoft.cognitiveservices.speech.transcription.ConversationTranscriber;
import com.opentool.ai.tool.utils.BinaryAudioStreamReader;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

/**
 * 文档参考：https://learn.microsoft.com/zh-cn/azure/ai-services/speech-service/how-to-recognize-speech?pivots=programming-language-java
 * / @Author: ZenSheep
 * / @Date: 2023/11/15 21:51
 */
public class SpeechRecognition {
    // This example requires environment variables named "SPEECH_KEY" and "SPEECH_REGION"
    private static String speechKey = "f258c08822184feea388f6b58160295e";
    private static String speechRegion = "eastasia";
    private static String filePath = "./temp/stt/voice/conversation/two/katiesteve.wav";

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey, speechRegion);
        speechConfig.requestWordLevelTimestamps();
//        speechConfig.setSpeechRecognitionLanguage("en-US"); // en-US、zh-CN

//        recognizeFromMicrophone(speechConfig);
//        recognizeFromAudioFile(speechConfig);
//        continuousRecognizeFromAudioFile(speechConfig);
        conversationTranscriptionFromAudioFile(speechConfig);
    }

    /**
     * 实时语音识别:使用 RecognizeOnceAsync 操作听录 30 秒以内的语音，或直到检测到静音。
     * @param speechConfig
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static void recognizeFromMicrophone(SpeechConfig speechConfig) throws InterruptedException, ExecutionException {
        AudioConfig audioConfig = AudioConfig.fromDefaultMicrophoneInput();
        SpeechRecognizer speechRecognizer = new SpeechRecognizer(speechConfig, audioConfig);

        System.out.println("Speak into your microphone.");
        Future<SpeechRecognitionResult> task = speechRecognizer.recognizeOnceAsync();
        SpeechRecognitionResult speechRecognitionResult = task.get();

        if (speechRecognitionResult.getReason() == ResultReason.RecognizedSpeech) {
            System.out.println("RECOGNIZED: Text=" + speechRecognitionResult.getText());
        }
        else if (speechRecognitionResult.getReason() == ResultReason.NoMatch) {
            System.out.println("NOMATCH: Speech could not be recognized.");
        }
        else if (speechRecognitionResult.getReason() == ResultReason.Canceled) {
            CancellationDetails cancellation = CancellationDetails.fromResult(speechRecognitionResult);
            System.out.println("CANCELED: Reason=" + cancellation.getReason());

            if (cancellation.getReason() == CancellationReason.Error) {
                System.out.println("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                System.out.println("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
                System.out.println("CANCELED: Did you set the speech resource key and region values?");
            }
        }

        System.exit(0);
    }

    /**
     * 文件语音识别：转译 30 秒内的语音
     * @param speechConfig
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void recognizeFromAudioFile(SpeechConfig speechConfig) throws ExecutionException, InterruptedException {
        AudioConfig audioConfig = AudioConfig.fromWavFileInput("./temp/stt/voice/audio.wav");
        SpeechRecognizer speechRecognizer = new SpeechRecognizer(speechConfig, audioConfig);

        Future<SpeechRecognitionResult> task = speechRecognizer.recognizeOnceAsync();
        SpeechRecognitionResult result = task.get();

        System.out.println("RECOGNIZED: Text=" + result.getText());
    }

    /**
     * 连续语音识别
     * @param speechConfig
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void continuousRecognizeFromAudioFile(SpeechConfig speechConfig) throws ExecutionException, InterruptedException, IOException {
        PullAudioInputStream pullAudioInputStream = AudioInputStream.createPullStream(new BinaryAudioStreamReader(filePath),
                AudioStreamFormat.getCompressedFormat(AudioStreamContainerFormat.ANY));
        AudioConfig audioConfig = AudioConfig.fromStreamInput(pullAudioInputStream);

        // 默认音频流格式为 WAV（16kHz 或 8kHz、16 位和单声道 PCM）。在 WAV/PCM 之外，还支持下面列出的压缩输入格式。
//        AudioConfig audioConfig = AudioConfig.fromWavFileInput(filePath);
        // 识别语句的偏移量和持续时间（recognized）
        speechConfig.requestWordLevelTimestamps();
        // Please see https://docs.microsoft.com/azure/cognitive-services/speech-service/language-support for all supported languages.
        AutoDetectSourceLanguageConfig autoDetectSourceLanguageConfig = AutoDetectSourceLanguageConfig.fromLanguages(Arrays.asList("en-US", "zh-CN", "de-DE"));

        // 1.初始化信号量
        Thread sttThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Semaphore stopTranslationWithFileSemaphore = new Semaphore(0);
                SpeechRecognizer speechRecognizer = new SpeechRecognizer(speechConfig, autoDetectSourceLanguageConfig, audioConfig);
                {
                    // 2.连续语音识别配置
                    // recognizing：事件信号，包含中间识别结果
                    speechRecognizer.recognizing.addEventListener((s, e) -> {
                        // 识别偏移量和持续时间: 一个时钟周期表示100纳秒，即一千万分之一秒
                        BigInteger offset =  e.getResult().getOffset().multiply(BigInteger.valueOf(100));
                        BigInteger duration = e.getResult().getDuration().multiply(BigInteger.valueOf(100));
                        String beginTimeStamp = convertNanoTimestamp(offset);
                        String endTimeStamp = convertNanoTimestamp(offset.add(duration));
                        // 输出识别结果（一句话）
                        System.out.println("[recognizing]-【" + beginTimeStamp + "," + endTimeStamp + "】:" + e.getResult().getText());
                    });

                    // recognized：包含最终识别结果的时间信号，指示成功的识别尝试
                    speechRecognizer.recognized.addEventListener((s, e) -> {
                        if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
                            System.out.println("[recognized]语音识别结果：");
                            // 识别语句后，即可获取已识别语音的偏移量和持续时间
                            // 一个时钟周期表示一百纳秒，即一千万分之一秒
                            BigInteger offset =  e.getResult().getOffset().multiply(BigInteger.valueOf(100));
                            BigInteger duration = e.getResult().getDuration().multiply(BigInteger.valueOf(100));

                            String beginTimeStamp = convertNanoTimestamp(offset);
                            String endTimeStamp = convertNanoTimestamp(offset.add(duration));

                            System.out.println("【" + beginTimeStamp + "," + endTimeStamp + "】:" + e.getResult().getText());
                        } else if (e.getResult().getReason() == ResultReason.NoMatch) {
                            System.out.println("无法匹配：语音不能被识别");
                        }
                    });

                    // canceled：时间信号，包含已取消的识别结果。这些结果指示因直接取消请求而取消的识别尝试。或者，他们指示传输协议失败。
                    speechRecognizer.canceled.addEventListener((s, e) -> {
                        System.out.println("[canceled]取消：Reason=" + e.getReason());

                        if (e.getReason() == CancellationReason.Error) {
                            System.out.println("[canceled]取消:ErrorCode=" + e.getErrorCode());
                            System.out.println("[canceled]取消:ErrorDetails=" + e.getErrorDetails());
                            System.out.println("[canceled]取消:Did you set the speech resource key and region values?");
                        }

                        stopTranslationWithFileSemaphore.release();
                    });

                    // sessionStarted: 事件信号，指示识别会话的开始
                    speechRecognizer.sessionStarted.addEventListener((s, e) -> {
                        System.out.println("\n    Session started event.");
                    });

                    // sessionStopped：事件信号，指示识别会话的结束
                    speechRecognizer.sessionStopped.addEventListener((s, e) -> {
                        System.out.println("\n    Session stopped event.");
                        stopTranslationWithFileSemaphore.release();
                    });
                }

                // 3.设置所有项后，开始识别
                // Starts continuous recognition. Uses StopContinuousRecognitionAsync() to stop recognition
                try {
                    speechRecognizer.startContinuousRecognitionAsync().get();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }

                // Waits for completion
                try {
                    stopTranslationWithFileSemaphore.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // Stops recognition
                try {
                    speechRecognizer.stopContinuousRecognitionAsync().get();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }

                speechConfig.close();
                audioConfig.close();
                speechRecognizer.close();

                System.exit(0);
            }
        });

        sttThread.start();
    }

    public static String convertNanoTimestamp(BigInteger nanoTimestamp) {
        // 将纳秒级时间戳转换为秒级时间戳
        long seconds = nanoTimestamp.longValue() / 1_000_000_000;
        // 获取纳秒部分
        int nanos = (int) (nanoTimestamp.longValue() % 1_000_000_000);

        // 将秒级时间戳转换为LocalDateTime对象
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(seconds, nanos, ZoneOffset.UTC);

        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm:ss.SSS");

        // 格式化时间并返回
        return dateTime.format(formatter);
    }

    public static void conversationTranscriptionFromAudioFile(SpeechConfig speechConfig) throws ExecutionException, InterruptedException, IOException {
        speechConfig.setSpeechRecognitionLanguage("en-US");
        AudioConfig audioInput = AudioConfig.fromWavFileInput(filePath);

        Semaphore stopRecognitionSemaphore = new Semaphore(0);

//        AutoDetectSourceLanguageConfig autoDetectSourceLanguageConfig = AutoDetectSourceLanguageConfig.fromLanguages(Arrays.asList("en-US", "zh-CN", "de-DE"));
        ConversationTranscriber conversationTranscriber = new ConversationTranscriber(speechConfig, audioInput);
        {
            // Subscribes to events.
            conversationTranscriber.transcribing.addEventListener((s, e) -> {
                System.out.println("TRANSCRIBING: Text=" + e.getResult().getText());
            });

            conversationTranscriber.transcribed.addEventListener((s, e) -> {
                if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
                    System.out.println("TRANSCRIBED: Text=" + e.getResult().getText() + " Speaker ID=" + e.getResult().getSpeakerId() );
                }
                else if (e.getResult().getReason() == ResultReason.NoMatch) {
                    System.out.println("NOMATCH: Speech could not be transcribed.");
                }
            });

            conversationTranscriber.canceled.addEventListener((s, e) -> {
                System.out.println("CANCELED: Reason=" + e.getReason());

                if (e.getReason() == CancellationReason.Error) {
                    System.out.println("CANCELED: ErrorCode=" + e.getErrorCode());
                    System.out.println("CANCELED: ErrorDetails=" + e.getErrorDetails());
                    System.out.println("CANCELED: Did you update the subscription info?");
                }

                stopRecognitionSemaphore.release();
            });

            conversationTranscriber.sessionStarted.addEventListener((s, e) -> {
                System.out.println("\n    Session started event.");
            });

            conversationTranscriber.sessionStopped.addEventListener((s, e) -> {
                System.out.println("\n    Session stopped event.");
            });

            conversationTranscriber.startTranscribingAsync().get();

            // Waits for completion.
            stopRecognitionSemaphore.acquire();

            conversationTranscriber.stopTranscribingAsync().get();
        }

        speechConfig.close();
        audioInput.close();
        conversationTranscriber.close();

        System.exit(0);
    }
}