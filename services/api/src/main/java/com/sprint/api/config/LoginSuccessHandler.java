package com.sprint.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.api.dto.user.CustomUserDetailsDto;
import com.sprint.api.service.redis.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;


/** 로그인 성공 핸들러 클래스
 * - 로그인이 성공하면 Redis에서
 * - 이전 토큰을 삭제(강제 로그아웃)하고 새 토큰 발급
 */

/** Redis -> email : refreshToken, 서버 메모리에 저장됨
 *  JWT -> UUID : 토큰 그 자체(문자열), 클라이언트에 저장됨
 */


@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);

        // Principal에서 CustomUserDetailsDto를 꺼냄
        CustomUserDetailsDto userDetails = (CustomUserDetailsDto) authentication.getPrincipal();

        // 유저 UUID 가져옴 (토큰용)
        String userId = userDetails.getUser().getId().toString();

        // 이메일 가져옴 (Redis Key용)
        String email = userDetails.getUsername();

        // Redis에서 기존 리프레시 토큰 삭제 (강제 로그아웃)
        redisService.deleteRefreshToken(email);

        // JWT 생성시 UUID 넣음
        String accessToken = jwtProvider.createAccessToken(userId);
        String refreshToken = jwtProvider.createRefreshToken(userId);

        // Redis에 새 리프레시 토큰 저장 (7일)
        redisService.saveRefreshToken(email, refreshToken, 60 * 60 * 24 * 7);

        // ================== [여기서부터 추가] ==================
        // 브라우저 쿠키 저장소에 REFRESH_TOKEN을 직접 심어줍니다.
        String cookieValue = "REFRESH_TOKEN=" + refreshToken +
                "; Path=/" +                // 모든 경로에서 쿠키 사용 가능
                "; HttpOnly" +            // JS에서 접근 불가 (보안)
                "; Max-Age=" + (60 * 60 * 24 * 7) + // 7일 유지
                "; SameSite=Lax";         // 크로스 도메인 설정 (필요시)

        response.addHeader("Set-Cookie", cookieValue);
        // =====================================================

        response.setContentType("application/json;charset=UTF-8");
        Map<String, String> tokens = Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );

        response.getWriter().write(objectMapper.writeValueAsString(tokens));
    }
}
