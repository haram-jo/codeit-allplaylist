package com.sprint.api.repository.playlist;

import com.sprint.api.entity.playlists.Playlist;

import java.util.List;
import java.util.UUID;

/** 플레이리스트 커스텀 레포지토리 인터페이스
 * - 플레이리스트 조회 및 카운트 기능을 정의
 */
public interface PlaylistRepositoryCustom {

    // 커서 기준(마지막으로 본 위치)으로 다음 페이지 가져옴
    List<Playlist> findAllByCursor(String keywordLike, UUID ownerIdEqual, UUID subscriberIdEqual,
                                   String cursor, UUID idAfter, int limit,
                                   String sortDirection, String sortBy);

    // 조건에 맞는 플레이리스트 개수 카운트
    long countByConditions(String keywordLike, UUID ownerIdEqual, UUID subscriberIdEqual);
}