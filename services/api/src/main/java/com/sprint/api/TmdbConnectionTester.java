package com.sprint.api;
import com.fasterxml.jackson.databind.ObjectMapper; // 추가
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 애플리케이션 시작 시 TMDB API 연결 테스트를 수행하는 클래스
 */

@Component
@Slf4j
public class TmdbConnectionTester implements CommandLineRunner {

    private final RestClient tmdbRestClient;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 정렬용

    public TmdbConnectionTester(RestClient tmdbRestClient) {
        this.tmdbRestClient = tmdbRestClient;
    }

    @Override
    public void run(String... args) {
        log.info("=== TMDB 데이터 원본 확인 시작 ===");
        try {
            // 1. 일단 문자열로 통째로 가져오기
            String rawJson = tmdbRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/movie/popular")
                            .queryParam("language", "ko-KR")
                            .build())
                    .retrieve()
                    .body(String.class);

            // 2. JSON을 예쁘게 정렬해서 로그에 찍기 (Pretty Print)
            Object jsonObject = objectMapper.readValue(rawJson, Object.class);
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(jsonObject);

            log.info("전체 응답 데이터:\n{}", prettyJson);

        } catch (Exception e) {
            log.error("연결 테스트 중 에러: {}", e.getMessage());
        }
    }
}
