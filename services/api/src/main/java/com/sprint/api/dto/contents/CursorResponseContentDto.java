package com.sprint.api.dto.contents;

import java.util.List;
import java.util.UUID;

/**
 * 콘텐츠 목록 조회를 위한 커서 기반 페이징 응답 DTO
 */
public record CursorResponseContentDto(
        List<ContentDto> data,      // 데이터 목록
        String nextCursor,          // 다음 페이지를 조회하기 위한 메인 커서 (예: 정렬 기준값)
        UUID nextIdAfter,           // 동일 값 중복 방지를 위한 보조 커서 (보통 ID)
        boolean hasNext,            // 다음 데이터 존재 여부
        long totalCount,            // 전체 데이터 개수
        String sortBy,              // 적용된 정렬 기준 필드
        SortDirection sortDirection // 적용된 정렬 방향 (ENUM)
) {}