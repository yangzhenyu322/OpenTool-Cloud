package com.opentool.gateway.security.permit;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 自定义放开配置的URL
 * / @Author: ZenSheep
 * / @Date: 2024/1/2 20:04
 */
@Component
public class ScPermitUrlConfig {
    public static final String CONTEXT_PATH = "/api/v1";

    /**
     * 需要访问的url
     */
    private String[] permitUrl = {
            "/user/register"
//            "/**",
//            "/actuator/**",
//            "/api-account/account/**"
    };

    private String[] heartbeatUrl = {
//            "/heartbeat/**"
    };

    /**
     * 额外放开权限的url
     * @param urls 自定义的url
     * @return 自定义的url和监控中心需要访问的url集合
     */
    public String[] permit(String... urls) {
        Set<String> set = new HashSet<>();
        if (urls.length > 0) {
            Collections.addAll(set, addContextPath(urls));
        }

        // 放开权限的地址
        Collections.addAll(set, addContextPath(permitUrl));
        Collections.addAll(set, heartbeatUrl);

        return set.toArray(new String[set.size()]);
    }

    /**
     * 地址加访问前缀
     * @param urls
     * @return
     */
    private String[] addContextPath(String[] urls) {
        for (int i = 0; i < urls.length; i++) {
            urls[i] = CONTEXT_PATH + urls[i];
        }

        return urls;
    }
}
