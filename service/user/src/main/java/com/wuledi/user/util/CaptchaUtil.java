package com.wuledi.user.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;
/**
 * 验证码工具类
 */
@Component
public class CaptchaUtil {


    @Value("${wuledi.captcha.expiry-seconds}")
    private long captchaExpireSeconds;
    private final StringRedisTemplate redisTemplate; // 用于操作Redis
    private final Random random = new Random(); // 用于生成随机数

    public CaptchaUtil(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 生成随机验证码
     *
     * @return 生成的验证码
     */
    public String generateCaptcha(String email) {
        String captcha = String.valueOf(random.nextInt(900000) + 100000); // 生成6位数字验证码
        storeCaptcha(email, captcha); // 将验证码存储到Redis中
        return captcha;
    }

    /**
     * 验证用户输入的验证码是否正确
     *
     * @param email       用户的邮箱地址
     * @param userCaptcha 用户输入的验证码
     * @return 验证结果
     */
    public boolean verifyCaptcha(String email, String userCaptcha) {
        String redisKey = buildRedisKey(email); // 构建Redis键
        String storedCaptcha = redisTemplate.opsForValue().get(redisKey); // 从Redis中获取存储的验证码
        if (storedCaptcha == null || !storedCaptcha.equals(userCaptcha)) { // 验证码不存在或不匹配
            return false;
        }
        redisTemplate.delete(redisKey); // 验证成功后删除验证码
        return true;
    }

    /**
     * 将验证码存储到 Redis 中
     *
     * @param email   用户的邮箱地址
     * @param captcha 生成的验证码
     */
    public void storeCaptcha(String email, String captcha) {
        String redisKey = buildRedisKey(email); // 设置Redis键
        // 将验证码存储到Redis中，并设置过期时间为5分钟
        redisTemplate.opsForValue().set(redisKey, captcha, captchaExpireSeconds, TimeUnit.SECONDS);
    }


    /**
     * 构建 Redis 键
     *
     * @param email 用户的邮箱地址
     * @return Redis 键
     */
    private String buildRedisKey(String email) {
        return "wuledi:user:email:" + email + ":captcha";
    }
}
