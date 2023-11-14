package com.opentool.ai.tool.TTS;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/10/30 10:38
 */

import com.microsoft.cognitiveservices.speech.*;

import java.util.Scanner;
import java.util.concurrent.Future;

/**
 * Quickstart: synthesize speech using the Speech SDK for Java.
 */
public class Main {

    /**
     * @param args Arguments are ignored in this sample.
     */
    public static void main(String[] args) {

        // Replace below with your own subscription key
        String speechSubscriptionKey = "dbc728ec904c412db821e49484047059";
        // Replace below with your own service region (e.g., "westus").
        String serviceRegion = "eastasia";

        // Creates an instance of a speech synthesizer using speech configuration with
        // specified
        // subscription key and service region and default speaker as audio output.
        try (SpeechConfig config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion)) {
            // Set the voice name, refer to https://aka.ms/speech/voices/neural for full
            // list.
            config.setSpeechSynthesisVoiceName("en-US-AriaNeural");
            try (SpeechSynthesizer synth = new SpeechSynthesizer(config)) {

                assert (config != null);
                assert (synth != null);

                int exitCode = 1;

                System.out.println("Type some text that you want to speak...");
                System.out.print("> ");
                String text = new Scanner(System.in).nextLine();

                Future<SpeechSynthesisResult> task = synth.SpeakTextAsync(text);
                assert (task != null);

                SpeechSynthesisResult result = task.get();
                assert (result != null);

                if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                    System.out.println("Speech synthesized to speaker for text [" + text + "]");
                    exitCode = 0;
                } else if (result.getReason() == ResultReason.Canceled) {
                    SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails
                            .fromResult(result);
                    System.out.println("CANCELED: Reason=" + cancellation.getReason());

                    if (cancellation.getReason() == CancellationReason.Error) {
                        System.out.println("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                        System.out.println("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
                        System.out.println("CANCELED: Did you update the subscription info?");
                    }
                }

                System.exit(exitCode);
            }
        } catch (Exception ex) {
            System.out.println("Unexpected exception: " + ex.getMessage());

            assert (false);
            System.exit(1);
        }
    }
}