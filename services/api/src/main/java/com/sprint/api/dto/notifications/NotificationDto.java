package com.sprint.api.dto.notifications;

import com.sprint.api.entity.notifications.Notification;

import java.time.LocalDateTime;
import java.util.UUID;

/** 개별 알림 상세 정보 담는 DTO
 */
public record NotificationDto(
        UUID id,
        LocalDateTime createdAt,
        String receiverId,
        String title,
        String content,
        NotificationLevel level
) {
    // 엔티티를 DTO로 변환하는 편의 메서드
    public static NotificationDto from(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getCreatedAt(),
                notification.getReceiver().getId(),
                notification.getTitle(),
                notification.getContent(),
                notification.getLevel()
        );
    }
}