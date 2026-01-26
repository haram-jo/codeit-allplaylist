package com.sprint.batch.job;

import com.sprint.batch.listener.BatchJobExecutionListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 공장 조립도
 * - 모든 부품 조리후, 영화 정보 수집 공정을 완성하는 클래스
 * */

@Configuration
@RequiredArgsConstructor
public class TmdbBatchJobConfig {

    private final JobRepository jobRepository;
    private final Step tmdbStep;
    private final BatchJobExecutionListener batchJobExecutionListener;

    @Bean
    public Job tmdbJob() {
        return new JobBuilder("tmdbJob", jobRepository)
                .listener(batchJobExecutionListener) // ✅ 정답
                .start(tmdbStep)
                .build();
    }
}