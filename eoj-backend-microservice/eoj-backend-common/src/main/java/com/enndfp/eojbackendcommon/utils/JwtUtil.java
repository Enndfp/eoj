package com.enndfp.eojbackendcommon.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类
 *
 * @author Enndfp
 */
public class JwtUtil {

    /**
     * TOKEN的有效期：默认设置为7天（单位：秒）
     */
    private static final int TOKEN_TIME_OUT = 7 * 24 * 3600;

    /**
     * 加密密钥，用于签名JWT
     */
    private static final String TOKEN_SECRET = "enndfp";

    /**
     * 生成JWT Token
     *
     * @param params 存放在Token中的自定义信息，通常是用户信息、角色等
     * @return 生成的JWT Token
     */
    public static String getToken(Map<String, Object> params) {
        // 获取当前时间
        long currentTime = System.currentTimeMillis();
        
        // 生成JWT
        return Jwts.builder()
                // 使用HS512签名算法，签名密钥为TOKEN_SECRET
                .signWith(SignatureAlgorithm.HS512, TOKEN_SECRET)
                // 设置过期时间：当前时间 + 有效期（TOKEN_TIME_OUT秒）
                .setExpiration(new Date(currentTime + TOKEN_TIME_OUT * 1000))
                // 将自定义的claims（用户信息等）添加到JWT中
                .addClaims(params)
                // 构建并返回JWT Token
                .compact();
    }

    /**
     * 从JWT Token中解析并获取Claims信息
     *
     * @param token JWT Token
     * @return 解析出来的Claims对象，包含JWT中的自定义信息
     */
    public static Claims getClaims(String token) {
        // 解析JWT Token并获取Claims
        return Jwts.parser()
                // 使用TOKEN_SECRET密钥解析JWT
                .setSigningKey(TOKEN_SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证Token是否有效
     * 
     * 1. Token是否为空
     * 2. 是否能够成功解析并验证签名
     * 
     * @param token JWT Token
     * @return true-有效，false-失效
     */
    public static boolean verifyToken(String token) {
        // 如果Token为空，直接返回失效
        if (StringUtils.isEmpty(token)) {
            return false;
        }

        try {
            // 尝试解析JWT Token，如果抛出异常则表示Token无效
            Claims claims = Jwts.parser()
                    .setSigningKey(TOKEN_SECRET)
                    .parseClaimsJws(token)
                    .getBody();
            
            // 如果没有抛出异常，说明Token有效
        } catch (Exception e) {
            // 捕获解析异常，Token无效
            return false;
        }

        // Token有效
        return true;
    }
}
