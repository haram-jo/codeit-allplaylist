package com.sprint.api.repository.notification;

/* NotificationRepository에 커스텀 메서드 추가하기 위한 인터페이스
  - 복잡한 쿼리 메서드 시그니처 정의
 */

import com.sprint.api.dto.contents.SortDirection;
import com.sprint.api.dto.notifications.CursorResponseNotificationDto;

import java.util.UUID;

public interface NotificationRepositoryCustom {
    CursorResponseNotificationDto findAllByCursor(
            String userId, String cursor, UUID idAfter, int limit,
            SortDirection sortDirection, String sortBy);
}
