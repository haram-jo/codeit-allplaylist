package com.sprint.api.repository.contents;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.api.dto.contents.SortDirection;
import com.sprint.api.entity.contents.Contents;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.sprint.api.entity.contents.QContents.contents;

/**
 * Querydsl을 활용한 콘텐츠 조회 커스텀 리포지토리 구현체
 * - 동적 쿼리 작성을 통해 다양한 필터링 및 정렬 기능 제공
 */

@RequiredArgsConstructor
public class ContentsRepositoryCustomImpl implements ContentsRepositoryCustom {

    // JPA 쿼리를 자바 코드로 짤 수 있게 해주는 공장
    private final JPAQueryFactory queryFactory; // Config에서 등록한 빈이 여길로 들어옴

    @Override
    public List<Contents> findAllByCursor(String typeEqual, String keywordLike, List<String> tagsIn,
                                          String cursor, UUID idAfter, int limit,
                                          String sortBy, SortDirection sortDirection) {

        // 1. 정렬 방향 결정 (기본값 DESC)
        Order order = (sortDirection == SortDirection.ASCENDING) ? Order.ASC : Order.DESC;

        return queryFactory
                .selectFrom(contents)
                .where(
                        typeEq(typeEqual),
                        titleLike(keywordLike),
                        // 핵심: 복합 커서 조건
                        cursorCondition(cursor, idAfter, sortBy, sortDirection)
                )
                .limit(limit + 1)
                // 중요: 메인 정렬 기준과 ID 정렬 기준의 방향을 반드시 일치시켜야 인덱스를 타고 중복이 없음
                .orderBy(
                        new OrderSpecifier<>(order, getSortPath(sortBy)),
                        new OrderSpecifier<>(order, contents.id)
                )
                .fetch();
    }

    private BooleanExpression cursorCondition(String cursor, UUID idAfter, String sortBy, SortDirection direction) {
        // 커서나 보조 커서(idAfter)가 없으면 첫 페이지로 간주
        if (cursor == null || cursor.isEmpty() || idAfter == null) {
            return null;
        }

        boolean isDesc = (direction == SortDirection.DESCENDING);

        try {
            // 1. 인기순 (watcherCount)
            if ("watcherCount".equals(sortBy)) {
                long count = Long.parseLong(cursor);
                return isDesc
                        ? contents.watcherCount.lt(count).or(contents.watcherCount.eq(count).and(contents.id.lt(idAfter)))
                        : contents.watcherCount.gt(count).or(contents.watcherCount.eq(count).and(contents.id.gt(idAfter)));
            }

            // 2. 평점순 (rate 또는 averageRating) - 이미지 에러 해결 구간
            if ("rate".equals(sortBy) || "averageRating".equals(sortBy)) {
                // 소수점이 없는 정수형(int)이므로 Integer.parseInt 사용
                int rating = Integer.parseInt(cursor);

                return isDesc
                        ? contents.averageRating.lt(rating).or(contents.averageRating.eq(rating).and(contents.id.lt(idAfter)))
                        : contents.averageRating.gt(rating).or(contents.averageRating.eq(rating).and(contents.id.gt(idAfter)));
            }

            // 3. 최신순 (createdAt)
            LocalDateTime time = LocalDateTime.parse(cursor);
            return isDesc
                    ? contents.createdAt.lt(time).or(contents.createdAt.eq(time).and(contents.id.lt(idAfter)))
                    : contents.createdAt.gt(time).or(contents.createdAt.eq(time).and(contents.id.gt(idAfter)));

        } catch (Exception e) {
            // 파싱 중 에러 발생 시(데이터 포맷 불일치 등) 안전하게 첫 페이지 조회로 유도
            return null;
        }
    }

    // 정렬 경로 추출 헬퍼 메서드
    private com.querydsl.core.types.dsl.ComparableExpressionBase<?> getSortPath(String sortBy) {
        if ("watcherCount".equals(sortBy)) return contents.watcherCount;
        if ("rate".equals(sortBy) || "averageRating".equals(sortBy)) return contents.averageRating;
        return contents.createdAt;
    }

    // --- 필터 로직 (queryFactory가 쿼리를 만들 때 사용) ---

        // 1. 카테고리 필터:  해당 카테고리(영화, 스포츠 등)로 필터링
    private BooleanExpression typeEq(String type) {
        return (type != null && !type.isEmpty() && !"ALL".equalsIgnoreCase(type))
                ? contents.type.eq(type) : null;
    }

        // 2. 검색창에 입력한 글자가 제목에 포함되어 있는지 확인
    private BooleanExpression titleLike(String keyword) {
        return (keyword != null && !keyword.isEmpty())
                ? contents.title.contains(keyword) : null;
    }

    // 3. 커서 필터: 현재 정렬 기준에 따라 '이전 데이터'보다 작은 값을 가져옴
    private BooleanExpression cursorLt(String cursor, String sortBy) {
        if (cursor == null || cursor.isEmpty()) return null;

        try {
            if ("watcherCount".equals(sortBy)) {
                return contents.watcherCount.lt(Long.parseLong(cursor));
            }

            if ("averageRating".equals(sortBy)) {
                return contents.averageRating.lt(Integer.parseInt(cursor));
            }

            // 최신순
            return contents.createdAt.lt(LocalDateTime.parse(cursor));

        } catch (Exception e) {
            // cursor 타입이 안 맞으면 첫 페이지로 간주
            return null;
        }
    }

    // 4. 정렬: 사용자가 UI에서 선택한 기준 적용
    private OrderSpecifier<?> getOrderBy(String sortBy, SortDirection direction) {
        Order order = direction == SortDirection.DESCENDING ? Order.DESC : Order.ASC;

        if ("watcherCount".equals(sortBy)) {
            return new OrderSpecifier<>(order, contents.watcherCount);
        }

        if ("averageRating".equals(sortBy)) {
            return new OrderSpecifier<>(order, contents.averageRating);
        }

        return new OrderSpecifier<>(order, contents.createdAt); // 기본값 최신순
    }
}
