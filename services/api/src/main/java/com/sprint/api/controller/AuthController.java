package com.sprint.api.controller;

import com.sprint.api.config.JwtProvider;
import com.sprint.api.dto.user.JwtDto;
import com.sprint.api.dto.user.UserDto;
import com.sprint.api.repository.user.UserRepository;
import com.sprint.api.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 인증 컨트롤러
 * - 리프레시 토큰을 이용한 액세스 토큰 재발급 처리
 * - Redis와 JWT를 함께 사용하여 보안 강화
 */

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    private final UserRepository userRepository;

    @PostMapping("/refresh")
    public ResponseEntity<?> reissueToken(
            @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshToken) {

        // 1. 쿠키 검증
        if (refreshToken == null || !jwtProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(401).body("리프레시 토큰이 유효하지 않습니다.");
        }

        // 2. 토큰에서 식별자 추출 (LoginSuccessHandler가 userId를 넣었으므로 userId가 나옴)
        String userIdStr = jwtProvider.getEmail(refreshToken);

        // 3. Redis 조회를 위해 email 찾기 (핸들러가 email을 키로 저장했기 때문)
        var user = userRepository.findById(userIdStr)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        String email = user.getEmail();

        // 4. Redis 대조
        String savedToken = redisService.getRefreshToken(email);
        if (savedToken == null || !savedToken.equals(refreshToken)) {
            return ResponseEntity.status(401).body("토큰 정보가 일치하지 않습니다.");
        }

        // 5. 새 토큰 생성 (기존 로직과 동일하게 userId 주입)
        String newAccess = jwtProvider.createAccessToken(userIdStr);
        String newRefresh = jwtProvider.createRefreshToken(userIdStr);

        // 6. Redis 및 응답 갱신
        redisService.saveRefreshToken(email, newRefresh, 60 * 60 * 24 * 7);

        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .profileImageUrl(user.getProfileImageUrl())
                .build();

        JwtDto jwtDto = JwtDto.builder()
                .accessToken(newAccess)
                .userDto(userDto)
                .build();

        return ResponseEntity.ok(jwtDto);
    }
}
