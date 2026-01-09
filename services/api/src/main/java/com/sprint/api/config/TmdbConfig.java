package com.sprint.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/**
 * TMDB API 연동 설정
 */

@Configuration
public class TmdbConfig {

    // 바꾼 이름으로 매핑
    @Value("${custom.tmdb.access-token}")
    private String accessToken;

    @Bean //빈 등록, 다른 클래스에 주입해서 사용 가능
    public RestClient tmdbRestClient() {
        return RestClient.builder()
                //TMDB API의 기본주소
                .baseUrl("https://api.themoviedb.org/3")
                //헤더 인증
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                //응답 타입 JSON
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
