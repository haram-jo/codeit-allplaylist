package com.sprint.api.service.notification;


import com.sprint.api.common.exception.CustomException;
import com.sprint.api.common.exception.ErrorCode;
import com.sprint.api.dto.contents.SortDirection;
import com.sprint.api.dto.notifications.CursorResponseNotificationDto;
import com.sprint.api.dto.notifications.NotificationDto;
import com.sprint.api.dto.notifications.NotificationLevel;
import com.sprint.api.entity.notifications.Notification;
import com.sprint.api.entity.user.User;
import com.sprint.api.repository.notification.NotificationRepository;
import com.sprint.api.repository.user.UserRepository;
import com.sprint.api.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final SseService sseService;

    /**
     * 알림 목록 조회 (커서 페이지네이션)
     */
    public CursorResponseNotificationDto getNotifications(
            String userId, String cursor, UUID idAfter, int limit,
            SortDirection sortDirection, String sortBy
    ) {
        // Querydsl 커스텀 레포지토리 메서드 호출
        return notificationRepository.findAllByCursor(
                userId, cursor, idAfter, limit, sortDirection, sortBy);
    }

    /**
     * 알림 읽음 처리
     */
    @Transactional
    public void readNotification(String userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("알림을 찾을 수 없습니다."));

        // 본인 알림인지 검증
        if (!notification.getReceiver().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        notification.markAsRead(); // 엔티티 내부의 readAt 업데이트 로직 실행
    }

    /**
     * 알림 생성 및 실시간 전송
     */
    @Transactional
    public void createNotification(String receiverId, String title, String content, NotificationLevel level) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        //DB 저장
        Notification notification = Notification.builder()
                .receiver(receiver)
                .title(title)
                .content(content)
                .level(level)
                .build();

        notificationRepository.save(notification);

        //SSE 실시간 전송
        sseService.sendNotification(receiverId, NotificationDto.from(notification));
    }
}

