package com.sprint.api.dto.contents;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 콘텐츠 등록을 위한 요청 DTO
 */

public record ContentCreateRequest(
        @NotBlank(message = "콘텐츠 타입은 필수입니다.")
        String type, // movie, tvSeries, sport

        @NotBlank(message = "제목은 필수입니다.")
        String title,

        @NotBlank(message = "설명은 필수입니다.")
        String description,

        List<String> tags
) {}
