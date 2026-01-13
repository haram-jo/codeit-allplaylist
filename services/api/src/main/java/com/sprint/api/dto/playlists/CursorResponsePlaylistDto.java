package com.sprint.api.dto.playlists;

import java.util.List;
import java.util.UUID;

/* 플레이리스트 목록을 커서 기반 페이지네이션으로 보여줄 때
 */

public record CursorResponsePlaylistDto(
        List<PlaylistDto> data,
        String nextCursor,
        UUID nextIdAfter,
        boolean hasNext,
        long totalCount,
        String sortBy,
        SortDirection sortDirection
) {
    public enum SortDirection {
        ASCENDING, DESCENDING
    }
}