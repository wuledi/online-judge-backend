package com.wuledi.common.util;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * 签名工具类
 */
public class SignUtils {

    // 统一入口：所有类型均转为JSON处理
    public static String getSign(String key, String secretKey) {
        return DigestUtils.md5DigestAsHex((key + secretKey).getBytes(StandardCharsets.UTF_8));
    }


    // 验证签名
    public static boolean verifySign(String sign, String key, String secretKey) {
        return sign.equals(getSign(key, secretKey));
    }
}