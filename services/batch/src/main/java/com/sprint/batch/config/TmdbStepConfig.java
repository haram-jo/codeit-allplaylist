package com.sprint.batch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/** 작업 단계 설정
 * - 배치 작업의 개별 단계를 정의하는 구성 클래스
 * */

@Configuration
@RequiredArgsConstructor
public class TmdbStepConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Step tmdbStep() {
        return new StepBuilder("tmdbStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    return org.springframework.batch.repeat.RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
