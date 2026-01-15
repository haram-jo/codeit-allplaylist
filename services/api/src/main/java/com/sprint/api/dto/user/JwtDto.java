package com.sprint.api.dto.user;

import lombok.Builder;


/** 로그인 성공시 frontend에 전달할 JWT 토큰과 유저 정보 DTO
 *
 */
@Builder
public record JwtDto(
        UserDto userDto, // 유저 정보
        String accessToken
) {}
