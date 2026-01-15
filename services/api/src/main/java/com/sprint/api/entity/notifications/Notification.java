package com.sprint.api.entity.notifications;

import com.sprint.api.common.BaseEntity;
import com.sprint.api.dto.notifications.NotificationLevel;
import com.sprint.api.entity.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter @NoArgsConstructor
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver; // 알림 수신자

    @Column(nullable = false)
    private String title;
    @Column(nullable = false, length = 200)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationLevel level; // INFO, WARNING, ERROR 등

    private LocalDateTime readAt; // 읽은 시간

    @Builder
    public Notification(User receiver, String title, String content, NotificationLevel level) {
        this.receiver = receiver;
        this.title = title;
        this.content = content;
        this.level = level;
    }

    // 알림 읽음 처리 메서드
    public void markAsRead() {
        if (this.readAt == null) {
            this.readAt = LocalDateTime.now();
        }
    }
}
