package com.sprint.api.dto.playlists;

import java.util.List;
import java.util.UUID;

/* 플레이리스트에 담긴 콘텐츠 정보 보여줄 때
* */

public record ContentSummary (
     UUID id,
     ContentType type,
     String title,
     String description,
     String thumbnailUrl,
     List<String> tags,
     Double averageRating,
     Integer reviewCount
){}