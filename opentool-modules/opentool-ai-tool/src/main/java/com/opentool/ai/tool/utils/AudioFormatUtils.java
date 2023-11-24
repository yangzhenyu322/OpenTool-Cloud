package com.opentool.ai.tool.utils;

    /**
     * / @Author: ZenSheep
     * / @Date: 2023/10/29 22:34
     */
    public class AudioFormatUtils {
    public static final String RAW_8KHZ_8BIT_MONO_MULAW = "raw-8khz-8bit-mono-mulaw";
    public static final String RAW_16KHZ_16BIT_MONO_PCM = "raw-16khz-16bit-mono-pcm";
    public static final String RIFF_8KHZ_8BIT_MONO_MULAW = "riff-8khz-8bit-mono-mulaw";
    public static final String RIFF_16KHZ_16BIT_MONO_PCM = "riff-16khz-16bit-mono-pcm";
    public static final String RIFF_24KHZ_16BIT_MONO_PCM = "riff-24khz-16bit-mono-pcm"; // wav文件格式
    public static final String RIFF_44100HZ_16BIT_MONO_PCM = "riff-44100hz-16bit-mono-pcm";
    public static final String RIFF_48KHZ_16BIT_MONO_PCM = "riff-48khz-16bit-mono-pcm";
    public static final String WEBM_24KHZ_16BIT_MONO_OPUS = "webm-24khz-16bit-mono-opus";  // webm文件格式
    public static final String WEBM_24KHZ_16BIT_24KBPS_MONO_OPUS = "webm-24khz-16bit-24kbps-mono-opus";
    public static final String AUDIO_24KHZ_48KBITRATE_MONO_MP3 = "audio-24khz-48kbitrate-mono-mp3"; // mp3文件格式(推荐)
    public static final String AUDIO_24KHZ_96KBITRATE_MONO_MP3 = "audio-24khz-96kbitrate-mono-mp3";
    public static final String AUDIO_48KHZ_192KBITRATE_MONO_MP3 = "audio-48khz-192kbitrate-mono-mp3";
    public static final String AUDIO_48KHZ_96KBITRATE_MONO_MP3 = "audio-48khz-96kbitrate-mono-mp3";
}