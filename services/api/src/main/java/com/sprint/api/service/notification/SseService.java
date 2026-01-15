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

        // 연결 종료 처리
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));

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
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notificationDto));
            } catch (IOException e) {
                emitters.remove(userId);
            }
        }
    }
}







