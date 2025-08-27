package com.wuledi.common.util;

import java.security.SecureRandom;
import java.util.Base64;

public class KeyGenerator {

    // 复用 SecureRandom 实例以提升性能
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    // 复用 Base64 编码器
    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();

    /**
     * 生成 AccessKey，长度为 16 字节（128 位）
     */
    public static String generateAccessKey() {
        return generateKey(16); // 128-bit key
    }

    /**
     * 生成 SecretKey，长度为 32 字节（256 位）
     */
    public static String generateSecretKey() {
        return generateKey(32); // 256-bit key
    }

    /**
     * 生成指定长度的 Base64 编码密钥
     *
     * @param lengthInBytes 密钥长度（单位：字节）
     * @return Base64 编码的字符串形式密钥
     */
    private static String generateKey(int lengthInBytes) {
        byte[] keyBytes = new byte[lengthInBytes];
        SECURE_RANDOM.nextBytes(keyBytes);
        return BASE64_ENCODER.encodeToString(keyBytes);
    }
}