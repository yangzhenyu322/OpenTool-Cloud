package com.opentool.system.domain.vo.response;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/10/19 16:01
 */
@Data
public class ChatResponse {
    // 问题消耗tokens
    @JsonProperty("question_tokens")
    private long questionTokens = 0;
}
