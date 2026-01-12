package com.sprint.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.api.service.redis.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;


/* 로그인 성공 핸들러 파일
 * - 로그인이 성공하면 Redis에서
 * - 이전 토큰을 삭제(강제 로그아웃)하고 새 토큰 발급
 */


@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK); // 200 상태 코드를 명시적으로 설정
        String email = authentication.getName();

        // Redis에서 기존 리프레시 토큰 삭제
        redisService.deleteRefreshToken(email);

        // JWT 생성
        String accessToken = jwtProvider.createAccessToken(email);
        String refreshToken = jwtProvider.createRefreshToken(email);

        // Redis에 새 리프레시 토큰 저장 (7일)
        redisService.saveRefreshToken(email, refreshToken, 60 * 60 * 24 * 7);

        // 응답 설정 (Swagger JwtDto 구조에 맞춰 JSON 반환)
        response.setContentType("application/json;charset=UTF-8");
        Map<String, String> tokens = Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );

        response.getWriter().write(objectMapper.writeValueAsString(tokens));
    }
}
