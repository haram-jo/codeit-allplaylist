package com.sprint.api.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/*
 로그인 성공시 기존 토큰을 지우고 새 토큰을 저장하는 파일
  - StringRedisTemplate을 사용하여 Redis에 문자열 데이터 CRUD
*/

@Service
@RequiredArgsConstructor
public class RedisService {

    // 스프링이 제공하는 Redis
    private final StringRedisTemplate redisTemplate;

    //기존 토큰 삭제 (강제 로그아웃)
    public void deleteRefreshToken(String email) {
        redisTemplate.delete("RT:" + email);
    }

    // 새 토큰 저장
    public void saveRefreshToken(String email,String refreshToken, long durationSeconds) {
        redisTemplate.opsForValue().set(
                "RT:" + email,
                refreshToken,
                durationSeconds,
                TimeUnit.SECONDS); // 만료 시간 초 단위
    }
}
