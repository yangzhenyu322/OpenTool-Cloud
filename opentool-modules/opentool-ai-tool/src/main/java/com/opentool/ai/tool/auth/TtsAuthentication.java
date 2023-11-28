package com.opentool.ai.tool.auth;

import cn.hutool.core.util.StrUtil;
import com.opentool.ai.tool.cache.TtsLocalCache;
import com.opentool.ai.tool.constant.TtsConstant;
import com.opentool.ai.tool.http.HttpsConnection;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This class demonstrates how to get a valid O-auth token from
 * Azure Data Market.
 * / @Author: ZenSheep
 * / @Date: 2023/10/29 22:35
 */
@Slf4j
public class TtsAuthentication
{
    public String genAccessToken(String apiKey) {
        InputStream inSt;
        HttpsURLConnection webRequest;

        try {
            String accessToken = (String) TtsLocalCache.CACHE.get("tts-auth", false); // 有坑：isUpdataLastAccess默认为true，每次过期前访问会重置超时时间
            if (StrUtil.isEmpty(accessToken)) {
                webRequest = HttpsConnection.getHttpsConnection(TtsConstant.ACCESS_TOKEN_URI);
                webRequest.setDoInput(true);
                webRequest.setDoOutput(true);
                webRequest.setConnectTimeout(5000);
                webRequest.setReadTimeout(5000);
                webRequest.setRequestMethod("POST");

                byte[] bytes = new byte[0];
                webRequest.setRequestProperty("content-length", String.valueOf(bytes.length));
                webRequest.setRequestProperty("Ocp-Apim-Subscription-Key", apiKey);
                webRequest.connect();

                DataOutputStream dop = new DataOutputStream(webRequest.getOutputStream());
                dop.write(bytes);
                dop.flush();
                dop.close();

                inSt = webRequest.getInputStream();
                InputStreamReader in = new InputStreamReader(inSt);
                BufferedReader bufferedReader = new BufferedReader(in);
                StringBuffer strBuilder = new StringBuffer();
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    strBuilder.append(line);
                }

                bufferedReader.close();
                in.close();
                inSt.close();
                webRequest.disconnect();

                accessToken = strBuilder.toString();
                // 缓存token
                TtsLocalCache.CACHE.put("tts-auth", accessToken);
                log.info("生成新的tts-token: {}", accessToken);

            } else {
                log.info("从缓存中获取tts-token: {}", accessToken);
            }

            return accessToken;
        } catch (Exception e) {
            log.error("生成tts访问token失败 {}", e.getMessage());
        }

        return null;
    }
}
