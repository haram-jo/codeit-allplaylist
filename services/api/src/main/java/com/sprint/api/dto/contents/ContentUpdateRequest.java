package com.sprint.api.dto.contents;

import java.util.List;

/**
 * 콘텐츠 정보 수정을 위한 요청 DTO
 */
public record ContentUpdateRequest(
        String title,           // 콘텐츠 제목
        String description,     // 콘텐츠 설명
        List<String> tags       // 콘텐츠 태그 목록
) {}