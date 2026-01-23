package com.sprint.batch.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 성적 기록기
 * - 데이터 기록 장치
 * - 성공/실패/시간을 기록 -> 나중에 그래프로 볼 수 있음
 * */

@Component
@RequiredArgsConstructor
public class BatchJobMetricsListener {

    private final MeterRegistry meterRegistry;

    /** 배치 성공 횟수 */
    public void incrementSuccess(String jobName) {
        meterRegistry.counter("batch.job.success", "job", jobName).increment();
    }

    /** 배치 실패 횟수 */
    public void incrementFail(String jobName) {
        meterRegistry.counter("batch.job.fail", "job", jobName).increment();
    }

    /** 실행 시간 측정 시작 */
    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    /** 실행 시간 측정 종료 */
    public void stopTimer(Timer.Sample sample, String jobName) {
        sample.stop(
                Timer.builder("batch.job.duration")
                        .tag("job", jobName)
                        .register(meterRegistry)
        );
    }
}