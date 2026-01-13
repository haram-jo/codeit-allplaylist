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
        boolean subscribedByMe, // 구독 여부
        List<ContentSummary> contents
) {}