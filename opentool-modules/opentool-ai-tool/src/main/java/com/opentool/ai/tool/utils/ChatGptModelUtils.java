package com.opentool.ai.tool.utils;

import java.util.Arrays;
import java.util.List;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/12/9 16:22
 */
public class ChatGptModelUtils {
    // gpt3.5 模型列表
    private final static List<String> GPT3_5 = Arrays.asList("gpt-3.5-turbo-1106", "gpt-3.5-turbo-16k");
    // gpt4 模型列表
    private final static List<String> GPT4 = Arrays.asList("gpt-4-1106-preview", "gpt-4-vision-preview");
    // 纯文本模型列表
    private final static List<String> TEXT_GPT = Arrays.asList("gpt-3.5-turbo-1106", "gpt-3.5-turbo-16k", "gpt-4-1106-preview");
    // 文本及图片模型
    private final static List<String> VISION_GPT = Arrays.asList("gpt-4-vision-preview");

    public static boolean isGPT3_5(String model) {
        return GPT3_5.contains(model);
    }

    public static boolean isGPT4(String model) {
        return GPT4.contains(model);
    }

    public static boolean isTextGPT(String model) {
        return TEXT_GPT.contains(model);
    }

    public static boolean isVisionGPT(String model) {
        return VISION_GPT.contains(model);
    }

    /**
     * 通过模型名返回所采用的gpt对话策略类型
     * @param model
     * @return
     */
    public static String getStrategyType(String model) {
        if (isTextGPT(model)) {
            return "textChatGpt";
        } else if (isVisionGPT(model)) {
            return "visionChatGpt";
        }
        return null;
    }
}
