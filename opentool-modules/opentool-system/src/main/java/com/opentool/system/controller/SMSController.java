package com.opentool.system.controller;

import com.opentool.common.core.domain.R;
import com.opentool.system.service.ISMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SMS服务接口
 * @Author: ZenSheep
 * @Date: 2024/1/17 19:45
 */
@RefreshScope
@RestController
@RequestMapping("/sms")
public class SMSController {
    @Autowired
    private ISMSService smsService;

    /**
     * 获取手机验证码（可指定验证码长度）
     * @param phoneNumber 手机号码
     * @param codeLength 验证码长度（4~6位数字与字母组合）
     * @param validTime 验证码有效时间（单位：秒）
     * @return
     */
    @GetMapping("/code/{phoneNumber}/{codeLength}/{validTime}")
    public R<?> getPhoneCode(@PathVariable("phoneNumber") String phoneNumber,
                             @PathVariable("codeLength") int codeLength,
                             @PathVariable("validTime") Long validTime) {
        return smsService.sendPhoneCode(phoneNumber, codeLength, validTime);
    }

    /**
     * 验证用户输入的验证码是否正确
     * @param requestId 请求Id
     * @param inputCode 用户输入的验证码
     * @return
     */
    @GetMapping("/code/verification/{requestId}/{inputCode}")
    public R<?> verifyCode(@PathVariable("requestId") String requestId,
                           @PathVariable("inputCode") String inputCode) {
        return smsService.verifyCode(requestId, inputCode);
    }
}
