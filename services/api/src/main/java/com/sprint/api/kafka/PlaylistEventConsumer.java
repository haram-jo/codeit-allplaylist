package com.sprint.api.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.api.dto.notifications.NotificationLevel;
import com.sprint.api.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 플레이리스트 관련 이벤트를 처리하는 컨슈머 클래스
 * - kafka로부터 이벤트를 받는 역할을 담당
 * - 알림 생성 서비스 호출 및 처리
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaylistEventConsumer {
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @KafkaListener(topics = "playlist-subscription-topic", groupId = "playlist-subscription-group")
    public void consume(String message) {
        try {
            PlaylistSubscribedEvent event = objectMapper.readValue(message, PlaylistSubscribedEvent.class);
            log.info("✅ Kafka 이벤트 수신: {}", event);

        // 🔔 알림 전송 (DB 저장 + SSE 전송 통합 메서드)
            String content =
                    event.subscriberName() + "님이 당신의 플레이리스트를 구독했어요!";

            notificationService.createNotification(
                    event.ownerId().toString(),      // 알림 받을 사람
                    "플레이리스트 구독 알림",        // 제목
                    content,                        // 내용
                    NotificationLevel.INFO
            );

        } catch (Exception e) {
            log.error("Kafka 메시지 처리 실패", e);
        }
    }
}
