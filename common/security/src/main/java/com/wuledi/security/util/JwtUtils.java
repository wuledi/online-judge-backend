package com.wuledi.security.util;

import com.wuledi.common.util.ThrowUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component // Bean注册
@Slf4j
public class JwtUtils {

    @Value("${wuledi.config.jwt.issuer:wuledi}")
    private String issuer;         // 证书签发人

    @Value("${wuledi.config.jwt.secret:wuledi.config.jwt.secret.zhixinglu}")
    private String secret;          // 加密密钥

    @Value("${wuledi.config.jwt.expiry:720}")
    private long expiry;          // 失效时间（单位：hours}）

    @Value("${spring.application.name:undefined}")
    private String applicationName; // 应用名称

    @Value("${wuledi.author:wuledi}")
    private String author; // 作者信息

    /**
     * 根据指定的加密算法以及加密KEY创建一个Token数据
     *
     * @param encryptedData JWT数据需要携带的用户认证或授权数据
     * @return 生成的Token信息
     */
    public String generateToken(Map<String, Object> encryptedData) {

        ThrowUtils.throwIf(encryptedData == null, new JwtException("Token数据不能为空。"));

        // 设置签发时间和过期时间
        Date nowDate = new Date(); // 当前时间
        Date expiryDate = new Date(nowDate.getTime() + expiry * 1000 * 60 * 60); // 过期时间: 当前时间 + 有效期（单位：毫秒）

        // 设置 Header
        Map<String, Object> headers = new HashMap<>();
        headers.put("author", author); // 作者信息
        headers.put("module", applicationName); // 模块名称

        String tokenId = "token-" + UUID.randomUUID(); // 随机生成ID

        return Jwts.builder()
                .header().add(headers).and()         // 设置 Header
                .id(tokenId) // 设置 Token ID
                .claims().add(encryptedData).and() // 设置 Claims: 携带数据
                .issuer(issuer) // 设置签发人
                .issuedAt(nowDate) // 设置签发时间
                .expiration(expiryDate) // 设置过期时间
                .signWith(generalKey(), Jwts.SIG.HS256) // 指定算法和密钥
                .compact(); // 生成 Token
    }

    /**
     * 验证Token是否有效
     *
     * @param token 要验证的Token数据
     * @return Token有效返回true，否则返回false
     */
    public boolean verifyToken(String token) {
        ThrowUtils.throwIf(token == null, new JwtException("Token数据为空，无法验证。"));
        try {               // Token数据解析
            Jwts.parser()
                    .verifyWith(this.generalKey())         // 签名密钥
                    .build()           // 获取解析器
                    .parseSignedClaims(token)         // 解析Token数据
                    .getPayload();     // 获取数据
            return true;             // Token正确
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 根据已有的Token解析出所包含的数据信息
     *
     * @param token 要解析的Token数据
     * @return Jws接口实例（指定Token数据所包含的数据）
     * @throws JwtException JWT数据解析出现的异常
     */
    public Jws<Claims> parseToken(String token) throws JwtException {
        ThrowUtils.throwIf(token == null, new JwtException("Token数据为空，无法解析。"));
        try {
            return Jwts.parser() // 解析 Token
                    .verifyWith(generalKey()) // 验证密钥
                    .build() // 构建解析器
                    .parseSignedClaims(token); // 解析 Token
        } catch (Exception e) {
            // 处理过期异常
            if (e instanceof ExpiredJwtException) {
                log.info("Token已过期: {}", e.getMessage());
                throw new JwtException("Token已过期");
            } else {
                log.info("Token解析失败: {}", e.getMessage());
                throw new JwtException("Token解析失败");
            }
        }
    }


    /**
     * Token刷新（延缓失效）
     *
     * @param token 要延缓失效的Token数据
     * @return 新的Token数据，如果该Token已经失效，则返回null
     */
    public String refreshToken(String token) {
        ThrowUtils.throwIf(token == null, new JwtException("Token数据为空，无法刷新。"));
        try {
            Jws<Claims> claimsJws = parseToken(token); // 解析Token
            Claims payload = claimsJws.getPayload(); // 获取负载数据
            String subject = payload.getSubject(); // 获取原始主题
            // 从claims中获取加密数据，subject（对应createToken中传入的subject参数）
            Map<String, Object> encryptedData = (Map<String, Object>) payload.get(subject);
            return generateToken(encryptedData); // 使用原始的id和加密数据生成新Token
        } catch (JwtException e) {
            return null;
        }
    }

    /**
     * 获取当前JWT数据的加密KEY
     *
     * @return SecretKey接口实例
     */
    public SecretKey generalKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        // 检查密钥长度是否符合要求
        ThrowUtils.throwIf(keyBytes.length < 32, new JwtException("JWT 密钥长度不足 32 字节，请检查配置文件中的 wuledi.config.jwt.secret 值"));
        return Keys.hmacShaKeyFor(keyBytes); // 自动识别算法强度
    }
}
