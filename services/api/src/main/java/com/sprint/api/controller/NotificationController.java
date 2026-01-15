package com.sprint.api.controller;

import com.sprint.api.dto.contents.SortDirection;
import com.sprint.api.dto.notifications.CursorResponseNotificationDto;
import com.sprint.api.dto.user.CustomUserDetailsDto;
import com.sprint.api.service.notification.NotificationService;
import com.sprint.api.service.notification.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SseService sseService;


    /**
     * SSE 실시간 알림 구독
     * 프론트에서 EventSource를 통해 이 경로로 연결 시도
     */
    @GetMapping(value = "/api/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @AuthenticationPrincipal CustomUserDetailsDto userDetail,
            @RequestParam(name = "LastEventId", required = false) String lastEventId // 책갈피 역할
    ) {
        return sseService.subscribe(userDetail.getUserId().toString());
    }

    /**
     * 커서 기반 페이징을 이용한 알림 목록 조회
     */
    @GetMapping("/api/notifications")
    public ResponseEntity<CursorResponseNotificationDto> getNotifications(
            @AuthenticationPrincipal CustomUserDetailsDto userDetail, // 수정된 부분
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) UUID idAfter,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "DESCENDING") SortDirection sortDirection,
            @RequestParam(defaultValue = "createdAt") String sortBy
    ) {
        // UUID를 String으로 변환하여 서비스에 전달
        String userIdStr = userDetail.getUserId().toString();

        CursorResponseNotificationDto response = notificationService.getNotifications(
                userIdStr, cursor, idAfter, limit, sortDirection, sortBy);

        return ResponseEntity.ok(response);
    }

    /**
     * 알림 읽음 처리
     */
    @DeleteMapping("/api/notifications/{notificationId}")
    public ResponseEntity<Void> readNotification(
            @AuthenticationPrincipal CustomUserDetailsDto userDetail,
            @PathVariable UUID notificationId
    ) {
        // String 변환
        notificationService.readNotification(userDetail.getUserId().toString(), notificationId);
        return ResponseEntity.noContent().build();
    }
}
