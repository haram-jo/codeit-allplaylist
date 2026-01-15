package com.sprint.api.dto.playlists;

import java.util.List;
import java.util.UUID;

/* 플레이리스트 내부에 표시될 콘텐츠의 요약 정보 보여줄때
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