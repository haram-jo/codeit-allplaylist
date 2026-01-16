package com.sprint.api.service.notification;


import com.sprint.api.dto.notifications.NotificationDto;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** SSE 연결 서비스
 * - 구독, 전송 단계로 나누어짐
 */

@Service
public class SseService {

    // 사용자별 SSE 연결을 관리하는 맵
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * SSE Subscribe 구독 단계
     */
    public SseEmitter subscribe(String userId) {
        // 1시간 단위로 연결 유지 설정
        SseEmitter emitter = new SseEmitter(60 * 1000L * 60);
        emitters.put(userId, emitter);

        // [로그 추가] 구독 확인
        System.out.println("SSE 구독 성공 - 사용자 ID: " + userId);

        // 연결 종료 처리
        emitter.onCompletion(() -> {
            System.out.println("SSE 연결 종료 (Completion) - 사용자 ID: " + userId);
            emitters.remove(userId);
        });
        emitter.onTimeout(() -> {
            System.out.println("SSE 연결 시간초과 (Timeout) - 사용자 ID: " + userId);
            emitters.remove(userId);
        });

        // 503 에러 방지를 위한 초기 연결 메시지
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected!"));
        } catch (IOException e) {
            emitters.remove(userId);
        }

        return emitter;
    }

    /**
     * 실시간 알림 전송 단계
     * 서비스 계층의 createNotification() 내부에서 호출됨
     */
    public void sendNotification(String userId, NotificationDto notificationDto) {
        // [로그 추가] 전송 시도 시 사용자 ID 확인
        System.out.println("알림 전송 시도 - 타겟 사용자 ID: " + userId);
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                // [로그 추가] Emitter를 찾았을 때
                System.out.println("Emitter 발견! 데이터를 전송합니다: " + notificationDto.toString());
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notificationDto));

                // [로그 추가] 전송 완료 확인
                System.out.println("실시간 알림 전송 완료!");
            } catch (IOException e) {
                System.out.println("전송 중 에러 발생 - 연결을 제거합니다.");
                emitters.remove(userId);
            }
        } else {
            // [로그 추가] Emitter를 못 찾았을 때 (여기가 찍히면 ID 매칭 문제)
            System.out.println("Emitter를 찾지 못했습니다. 현재 연결된 사용자 목록: " + emitters.keySet());
        }
    }
}







