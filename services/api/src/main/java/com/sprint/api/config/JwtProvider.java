package com.sprint.api.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/* JWT 토큰 생성하는 파일
  - Access Token과 Refresh Token 생성 메서드 제공
  - 비밀 키는 애플리케이션 설정 파일에서 주입받아 사용
*/

@Component
public class JwtProvider {

    private final SecretKey key;

    private static final long ACCESS_TOKEN_EXPIRE = 1000L * 60 * 30; // 30분
    private static final long REFRESH_TOKEN_EXPIRE = 1000L * 60 * 60 * 24 * 7; // 7일

    // 생성자에서 설정 파일의 secret 값을 읽어와 Key 객체로 변환
    public JwtProvider(@Value("${jwt.secret}") String secret) {
        // 문자열 기반의 키를 HMAC SHA 알고리즘에 적합한 SecretKey 객체로 생성
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    //30분 짜리 토큰 생성 메서드
    public String createAccessToken(String email) {
        return createToken(email, ACCESS_TOKEN_EXPIRE);
    }

    // 7일 짜리 토큰 생성 메서드
    public String createRefreshToken(String email) {
        return createToken(email, REFRESH_TOKEN_EXPIRE);
    }

    // 실제 토큰 생성 메서드
    private String createToken(String email, long expireTime) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expireTime))// 만료 시간 설정
                .signWith(key)
                .compact();
    }

    // 토큰에서 이메일 추출 (검증용)
    public String getEmail(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            // parserBuilder() 대신 parser()를 사용하고 verifyWith(key)를 씁니다.
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

