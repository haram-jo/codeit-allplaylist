package com.sprint.batch.listener;

import com.sprint.batch.metrics.BatchJobMetricsListener;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

/** 현장 감독관
 * - 작업 시작 시 시계 켜고, 종료 후 결과 확인
 * - 배치 Job의 실행 전·후 시점에 Micrometer 커스텀 메트릭을 수집
 * */

@Component
@RequiredArgsConstructor
public class BatchJobExecutionListener implements JobExecutionListener {

    private final BatchJobMetricsListener batchJobMetrics;
    private Timer.Sample timerSample;

    @Override
    public void beforeJob(JobExecution jobExecution) {

        //배치 실행 시간 측정 시작
        timerSample = batchJobMetrics.startTimer();
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            batchJobMetrics.incrementSuccess(jobName);
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            batchJobMetrics.incrementFail(jobName);
        }

        // 실행 시간 기록
        batchJobMetrics.stopTimer(timerSample, jobName);
    }
}
