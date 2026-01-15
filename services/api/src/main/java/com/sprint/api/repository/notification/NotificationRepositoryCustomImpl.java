package com.sprint.api.repository.notification;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.api.dto.contents.SortDirection;
import com.sprint.api.dto.notifications.CursorResponseNotificationDto;
import com.sprint.api.dto.notifications.NotificationDto;
import com.sprint.api.entity.notifications.Notification;
import com.sprint.api.entity.notifications.QNotification;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


/* NotificationRepositoryCustom의 구현체
- 복잡한 쿼리 메서드 구현
 */

@RequiredArgsConstructor
public class NotificationRepositoryCustomImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public CursorResponseNotificationDto findAllByCursor(
            String userId, String cursor, UUID idAfter, int limit,
            SortDirection sortDirection, String sortBy) {

        QNotification n = QNotification.notification;

        // 1. 커서 조건 생성 (최신순 전용)
        BooleanExpression cursorCondition = createCursorCondition(cursor, idAfter, n);

        // 2. 알림 데이터 조회 (limit + 1 전략으로 다음 페이지 여부 확인)
        List<Notification> results = queryFactory
                .selectFrom(n)
                .where(
                        n.receiver.id.eq(userId), // 특정 사용자의 알림만 필터링
                        cursorCondition
                )
                .limit(limit + 1)
                .orderBy(n.createdAt.desc(), n.id.desc()) // 항상 최신순으로 정렬
                .fetch();

        // 3. 페이징 결과 가공 및 DTO 변환
        boolean hasNext = results.size() > limit;
        if (hasNext) results.remove(limit);

        List<NotificationDto> data = results.stream().map(NotificationDto::from).toList();

        // 4. 다음 요청을 위한 커서 정보 추출
        String nextCursor = results.isEmpty() ? null : results.get(results.size() - 1).getCreatedAt().toString();
        UUID nextIdAfter = results.isEmpty() ? null : results.get(results.size() - 1).getId();

        return new CursorResponseNotificationDto(
                data, nextCursor, nextIdAfter, hasNext,
                getCount(userId), sortBy, sortDirection.name()
        );
    }

    /**
     * 무한 스크롤용 커서 조건 생성 메서드
     * - 마지막으로 보여준 알림 이후의 데이터들만 가져오도록 필터 생성
     */
    private BooleanExpression createCursorCondition(String cursor, UUID idAfter, QNotification n) {
        // 첫 페이지 요청 시(커서 정보 없음) 필터링 없이 진행
        if (cursor == null || idAfter == null) return null;

        LocalDateTime cursorTime = LocalDateTime.parse(cursor);

        // 최신순 기준: 기준 시간보다 과거이거나, 시간은 같지만 ID가 기준 ID보다 작은 데이터를 가져옴
        return n.createdAt.lt(cursorTime)
                .or(n.createdAt.eq(cursorTime).and(n.id.lt(idAfter)));
    }

    /**
     * 전체 알림 개수 조회 메서드
     * - UI 상단의 총 개수 표시
     */
    private long getCount(String userId) {
        Long count = queryFactory.select(QNotification.notification.count())
                .from(QNotification.notification)
                .where(QNotification.notification.receiver.id.eq(userId))
                .fetchOne();
        return count != null ? count : 0L;
    }
}
