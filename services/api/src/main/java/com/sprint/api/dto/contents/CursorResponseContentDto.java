package com.sprint.api.dto.contents;

import java.util.List;
import java.util.UUID;

/**
 * 콘텐츠 목록 조회를 위한 커서 기반 페이징 응답 DTO
 * 껍데기 (CursorResponseContentDto): 페이징 정보 (다음 페이지 좌표, 남은 데이터 여부 등)
 * 내용물 (ContentDto): 실제 우리가 보여줄 콘텐츠 알맹이들
 */

public record CursorResponseContentDto(
        List<ContentDto> data,      // 데이터 목록
        String nextCursor,          // 다음 페이지를 조회하기 위한 메인 커서(정렬 기준값)
        UUID nextIdAfter,           // 이 다음 ID부터 보여줘(동일 값 중복 방지)
        boolean hasNext,            // 다음 페이지가 있는지 없는지(프론트 더보기 버튼)
        long totalCount,            // 전체 검색 결과가 총 몇 개인지
        String sortBy,              // 적용된 정렬 기준 필드
        SortDirection sortDirection // 정렬방향 (오래된순, 최신순)
) {}