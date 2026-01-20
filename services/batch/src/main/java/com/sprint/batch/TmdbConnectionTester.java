package com.sprint.batch; // 패키지 경로를 batch로 수정하세요

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.batch.service.contents.TmdbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

//@Component
@Slf4j
@RequiredArgsConstructor
public class TmdbConnectionTester implements CommandLineRunner {

    private final RestClient tmdbRestClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TmdbService tmdbService;


    @Override
    public void run(String... args) {
        log.info("=== TMDB 데이터 원본 확인 시작 ===");
        try {
            // TMDB 인기 영화 목록 호출
            String rawJson = tmdbRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/movie/popular")
                            .queryParam("language", "ko-KR")
                            .build())
                    .retrieve()
                    .body(String.class);

            Object jsonObject = objectMapper.readValue(rawJson, Object.class);
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(jsonObject);

            log.info("전체 응답 데이터:\n{}", prettyJson);

            tmdbService.createContentsFromTmdb();

        } catch (Exception e) {
            log.error("연결 테스트 중 에러: {}", e.getMessage());
        }
    }
}