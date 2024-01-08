package com.opentool.gateway.security.tool;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * / @Author: ZenSheep
 * / @Date: 2024/1/2 20:34
 */
public class JWTUtils {
    /** 自己设置的秘钥 **/
    private final static String SECRET = "OpenTool-Secret";
    /** 默认过期时间---1h **/
    private final static int EXPIRATION_TIME = 60 * 60;
    /** 请记住我的过期时间---7天 **/
    public final static int REMEMBER_ME = 7;

    /**
     * 创建新的token
     * @param payload 负荷（base64编码的用户信息）
     * @param expireTime 过期时间
     * @return
     */
    public static String creatToken(Map<String,String> payload, int expireTime){
        JWTCreator.Builder builder= JWT.create();
        Calendar instance=Calendar.getInstance(); //获取日历对象
        if(expireTime <=0) {
            instance.add(Calendar.SECOND,EXPIRATION_TIME); //默认一小时
        } else {
            instance.add(Calendar.SECOND,expireTime);
        }
        //为了方便只放入了一种类型
        payload.forEach(builder::withClaim);
        return builder.withExpiresAt(instance.getTime()).sign(Algorithm.HMAC256(SECRET));
    }

    /**
     * 从token中获取用户信息
     * @param token
     * @return
     */
    public static Map<String, Object> getTokenInfo(String token){
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
        Map<String, Claim> claims = verify.getClaims();
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String expired= dateTime.format(verify.getExpiresAt());
        Map<String,Object> map = new HashMap<>();
        claims.forEach((k,v)-> map.put(k,v.asString()));
        map.put("exp",expired);

        return map;
    }
}