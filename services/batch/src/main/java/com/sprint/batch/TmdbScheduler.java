package com.sprint.batch;

import com.sprint.batch.service.contents.TmdbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TmdbScheduler {

    private final TmdbService tmdbService;

    //@Scheduled(cron = "0 0 2 * * *") // 매일 새벽 2시에 실행
    @Scheduled(cron = "0 * * * * *") // 1분마다 실행
    //@Scheduled(cron = "0 */10 * * * *") // 10분마다 실행
    public void createContentsDaily() {
        log.info("=== TMDB 인기 영화 자동 등록 스케줄러 시작 ===");
        try {
            tmdbService.createContentsFromTmdb();
            log.info("=== TMDB 인기 영화 자동 등록 스케줄러 완료 ===");
        } catch (Exception e) {
            log.error("스케줄러 실행 중 에러 발생: {}", e.getMessage());
        }
    }
}