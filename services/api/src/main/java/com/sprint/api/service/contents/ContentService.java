package com.sprint.api.service.contents;

import com.sprint.api.dto.contents.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ContentService {


    // 콘텐츠 생성(어드민)
    ContentDto createContent(ContentCreateRequest request, MultipartFile thumbnail);

    // 콘텐츠 단건 상세 조회
    ContentDto getContent(UUID contentId);

    // 콘텐츠 삭제 (어드민)
    void deleteContent(UUID contentId);

    // 콘텐츠 수정 (어드민)
    ContentDto updateContent(UUID contentId, ContentUpdateRequest request, MultipartFile thumbnail);

    // 콘텐츠 목록 조회 (커서 페이지네이션)
    CursorResponseContentDto getContents(
            String typeEqual,
            String keywordLike,
            List<String> tagsIn,
            String cursor,
            UUID idAfter,
            int limit,
            String sortBy,
            SortDirection sortDirection
    );
}
