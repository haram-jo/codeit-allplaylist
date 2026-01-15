package com.sprint.api.dto.playlists;

import com.sprint.api.dto.user.UserSummary;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/* 플레이리스트 생성하고나서 201 응답값
 */

public record PlaylistDto(
        UUID id,
        UserSummary owner,
        String title,
        String description,
        LocalDateTime updatedAt,
        Long subscriberCount,
        boolean subscribedByMe, // 구독 여부 (false면 구독 버튼 보여줌, true면 구독 취소 버튼 보여줌)
        List<ContentSummary> contents // 플레이리스트에 포함된 콘텐츠 목록
) {}