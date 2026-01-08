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
public class ContentsRepositoryImpl implements ContentsRepositoryCustom {

    // JPA 쿼리를 자바 코드로 짤 수 있게 해주는 공장
    private final JPAQueryFactory queryFactory; // Config에서 등록한 빈이 여길로 들어옴

    @Override
    public List<Contents> findAllByCursor(String typeEqual, String keywordLike, List<String> tagsIn,
                                          String cursor, UUID idAfter, int limit,
                                          String sortBy, SortDirection sortDirection) {

        return queryFactory
                .selectFrom(contents)
                .where(
                        typeEq(typeEqual),      // 상단 탭(영화, 스포츠 등) 필터
                        titleLike(keywordLike),  // 검색창 키워드 필터
                        cursorLt(cursor, sortBy) // 페이징 처리
                )
                .limit(limit + 1)
                .orderBy(getOrderBy(sortBy, sortDirection), contents.id.asc())
                .fetch();
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

        //인기순
        if ("watcherCount".equals(sortBy)) {
            return contents.watcherCount.lt(Long.parseLong(cursor));
        }

        //평점순
        if ("averageRating".equals(sortBy)) {
            return contents.averageRating.lt(Integer.parseInt(cursor));
        }

        // 최신순
        return contents.createdAt.lt(LocalDateTime.parse(cursor));
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
