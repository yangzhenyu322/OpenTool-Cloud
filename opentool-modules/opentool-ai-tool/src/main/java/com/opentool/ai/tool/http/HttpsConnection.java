package com.opentool.ai.tool.http;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/10/29 22:36
 */

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;

public class HttpsConnection {

    public static HttpsURLConnection getHttpsConnection (String connectingUrl) throws Exception {
        URL url = new URL(connectingUrl);
        HttpsURLConnection webRequest = (HttpsURLConnection) url.openConnection();
        return webRequest;
    }
}