package com.sprint.api.repository.playlist;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.api.entity.playlists.Playlist;
import com.sprint.api.entity.playlists.QPlaylistSubscriptions;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.sprint.api.entity.playlists.QPlaylist.playlist;

/** 플레이리스트 커스텀 레포지토리 구현체
 * - Querydsl을 사용하여 동적 쿼리 (검색,정렬,페이징)을 처리
 */

@RequiredArgsConstructor
public class PlaylistRepositoryCustomImpl implements PlaylistRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 커서 기반 페이징을 이용한 목록 조회
     * @param cursor 현재 페이지의 마지막 기준값 (날짜 문자열 또는 구독자 수)
     * @param limit 가져올 개수
     */
    @Override
    public List<Playlist> findAllByCursor(String keywordLike, UUID ownerIdEqual, UUID subscriberIdEqual,
                                          String cursor, UUID idAfter, int limit,
                                          String sortDirection, String sortBy) {
        return queryFactory
                .selectFrom(playlist) // 플리에서 데이터 뽑아옴
                .where(
                        titleLike(keywordLike), // 검색창
                        ownerEq(ownerIdEqual), // 특정 사용자
                        subscriberEq(subscriberIdEqual), // <-- 추가
                        cursorLt(cursor, sortBy)
                )
                .limit(limit + 1)
                .orderBy(getOrderBy(sortBy, sortDirection),
                        playlist.id.asc())
                .fetch();
    }

    /**
     * 현재 검색/필터 조건에 맞는 전체 데이터 개수 조회
     */
    @Override
    public long countByConditions(String keywordLike, UUID ownerIdEqual, UUID subscriberIdEqual) {
        Long count = queryFactory
                .select(playlist.count())
                .from(playlist)
                .where(
                        titleLike(keywordLike),
                        ownerEq(ownerIdEqual),
                        subscriberEq(subscriberIdEqual)
                )
                .fetchOne();

        // 결과가 null이면 0을 반환, 아니면 값을 반환
        return count != null ? count : 0L;
    }

    // 제목 검색 조건 keyword
    private BooleanExpression titleLike(String keyword) {
        return (keyword != null && !keyword.isEmpty())
                ? playlist.title.contains(keyword) : null;
    }

    // 소유자 필터
    private BooleanExpression ownerEq(UUID ownerId) {
        // User ID가 String이므로 toString() 변환 비교
        return (ownerId != null)
                ? playlist.user.id.eq(ownerId.toString()) : null;
    }

    // 커서 기준 조건 (마지막 cursor보다 작은 값 가져옴)
    private BooleanExpression cursorLt(String cursor, String sortBy) {
        if (cursor == null || cursor.isEmpty()) return null;

        if ("subscribeCount".equals(sortBy)) {
            return playlist.subscriberCount.lt(Long.parseLong(cursor));
        }
        // 기본값: 최신순(updatedAt)
        return playlist.updatedAt.lt(LocalDateTime.parse(cursor));
    }

    // 정렬 기준 지정
    private OrderSpecifier<?> getOrderBy(String sortBy, String direction) {
        Order order = "ASCENDING".equalsIgnoreCase(direction) ? Order.ASC : Order.DESC;

        if ("subscribeCount".equals(sortBy)) {
            return new OrderSpecifier<>(order, playlist.subscriberCount);
        }
        return new OrderSpecifier<>(order, playlist.updatedAt);
    }

    /** 내가 구독한 플레이리스트 ID들만 조회하는 메서드
     * - 내가 만든 플레이리스트는 제외
     */
    private BooleanExpression subscriberEq(UUID subscriberId) {
        if (subscriberId == null) return null;

        // 1. 구독 정보가 있는 플리여야 함 (조인 필요)
        // 2. 동시에 소유자(owner)가 나인 것은 제외해야 함
        return playlist.id.in(
                queryFactory
                        .select(QPlaylistSubscriptions.playlistSubscriptions.playlist.id)
                        .from(QPlaylistSubscriptions.playlistSubscriptions)
                        .where(QPlaylistSubscriptions.playlistSubscriptions.user.id.eq(subscriberId.toString()))
        ).and(playlist.user.id.ne(subscriberId.toString())); // 내 플리는 제외
    }
}