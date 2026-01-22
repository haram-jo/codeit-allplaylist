package com.sprint.api.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * 플레이리스트 관련 이벤트를 발행 클래스
 * - 구독 소식을 kafka에 전달하는 역할을 담당
 */

@Component
@RequiredArgsConstructor
public class PlaylistEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "playlist-subscription-topic";

    public void send(PlaylistSubscribedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event); // 자바객체 -> JSON 문자열 변환
            kafkaTemplate.send(TOPIC, message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Kafka 메시지 직렬화 실패", e);
        }
    }
}
