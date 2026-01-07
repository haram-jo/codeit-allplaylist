package com.sprint.api.dto.contents;

import java.util.List;
import java.util.UUID;

/**
 * 200 성공시 응답용 DTO
 */
public record ContentDto(
        UUID id, // 콘텐츠 아이디
        String type, // 콘텐츠 타입
        String title, // 콘텐츠 제목
        String description, // 콘텐츠 설명
        String thumbnailUrl, // 콘텐츠 썸네일
        List<String> tags, // 태그
        Double averageRating, // 평점 (집계된 평균값)
        Integer reviewCount, // 리뷰수 (필터링할때 인기순)
        Long watcherCount // 시청자 수
) {}
