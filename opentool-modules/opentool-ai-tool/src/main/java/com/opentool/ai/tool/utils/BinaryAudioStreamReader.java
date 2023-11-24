package com.opentool.ai.tool.utils;

import com.microsoft.cognitiveservices.speech.audio.PullAudioInputStreamCallback;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;

/**
 * Speech Recognition - BinaryAudioStreamReader
 * / @Author: ZenSheep
 * / @Date: 2023/11/19 21:57
 */
@Slf4j
public class BinaryAudioStreamReader extends PullAudioInputStreamCallback {

    InputStream inputStream;

    public BinaryAudioStreamReader(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        inputStream = new FileInputStream(file);
    }

    public BinaryAudioStreamReader(URL url) throws IOException {
        inputStream = url.openStream();
    }

    @Override
    public int read(byte[] dataBuffer) {
        try {
            return inputStream.read(dataBuffer, 0, dataBuffer.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Closes the audio input stream.
     */
    @Override
    public void close() {
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
