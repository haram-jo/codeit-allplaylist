package com.sprint.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class TmdbConnectionTester implements CommandLineRunner {

    private final RestClient tmdbRestClient;

    public TmdbConnectionTester(RestClient tmdbRestClient) {
        this.tmdbRestClient = tmdbRestClient;
    }

    @Override
    public void run(String... args) {
        log.info("=== TMDB 연결 테스트 시작 ===");
        try {
            String result = tmdbRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/movie/popular")
                            .queryParam("language", "ko-KR")
                            .queryParam("page", 1)
                            .build())
                    .retrieve()
                    .body(String.class);

            log.info("TMDB 응답 성공!");
            log.info("응답 데이터 일부: {}", result.substring(0, 100) + "...");
        } catch (Exception e) {
            log.error("TMDB 연결 실패: {}", e.getMessage());
        }
    }
}
