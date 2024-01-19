package com.opentool.system.service;

import com.opentool.common.core.domain.R;

/**
 * SMS服务接口
 * @Author: ZenSheep
 * @Date: 2024/1/17 19:42
 */
public interface ISMSService {

    R<?> sendPhoneCode(String phoneNumber, int codeLength, Long validTime);

    R<?> verifyCode(String requestId, String inputCode);
}
