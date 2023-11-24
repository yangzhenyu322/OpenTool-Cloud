package com.opentool.ai.tool.domain.vo;

import lombok.Data;

/** stt 请求参数对象
 * / @Author: ZenSheep
 * / @Date: 2023/11/22 11:34
 */
@Data
public class SttRequest {
    private String uid; // user id
    private String urlPath; // url path
}
