package com.opentool.ai.tool.STT;

import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/11/15 21:51
 */
public class SpeechRecognition {
    // This example requires environment variables named "SPEECH_KEY" and "SPEECH_REGION"
    private static String speechKey = "f258c08822184feea388f6b58160295e";
    private static String speechRegion = "eastasia";

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey, speechRegion);
        speechConfig.setSpeechRecognitionLanguage("zh-CN"); // en-US
//        recognizeFromMicrophone(speechConfig);
        recognizeFromAudioFile(speechConfig);
    }

    /**
     * 文件语音识别：转译 30 秒内的语音
     * @param speechConfig
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void recognizeFromAudioFile(SpeechConfig speechConfig) throws ExecutionException, InterruptedException {
        AudioConfig audioConfig = AudioConfig.fromWavFileInput("./temp/tts/voice/temp.wav");
        SpeechRecognizer speechRecognizer = new SpeechRecognizer(speechConfig, audioConfig);

        Future<SpeechRecognitionResult> task = speechRecognizer.recognizeOnceAsync();
        SpeechRecognitionResult result = task.get();
        System.out.println("RECOGNIZED: Text=" + result.getText());
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
}