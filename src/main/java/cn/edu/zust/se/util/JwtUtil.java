package cn.edu.zust.se.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // 生成密钥
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 Token（存储用户ID和userName）
     */
    public String generateToken(Long userId, String userName, Integer admin) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("userName", userName)
                .claim("admin", admin)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 解析 Token，获取用户 ID
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey()) // 使用相同的签名密钥
                    .build()
                    .parseSignedClaims(token)    // 解析并验证签名
                    .getPayload();              // 获取声明（Claims）
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token 已过期！");
        } catch (JwtException e) {
            throw new RuntimeException("Token 无效！");
        }
    }

    /**
     * 验证 Token 有效性
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token 已过期！");
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Token 无效！");
        }
    }
}
