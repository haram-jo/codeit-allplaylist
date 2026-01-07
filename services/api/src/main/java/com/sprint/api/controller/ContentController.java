package com.sprint.api.controller;

import com.sprint.api.dto.contents.*;
import com.sprint.api.service.ContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor  //필수 필드 생성자 자동 생성
public class ContentController {

    private ContentService contentService;

    /** 콘텐츠 생성 (어드민)
     * - MultipartFile(thumbnail)은 필수아님
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ContentDto createContent(
            @RequestPart("request") @Valid ContentCreateRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
    ) {
        return contentService.createContent(request, thumbnail);
    }

    /**
     * 콘텐츠 단건 상세 조회
     */
    @GetMapping("/{contentId}")
    public ContentDto getContent(@PathVariable UUID contentId) {
        return contentService.getContent(contentId);
    }

    /**
     * 콘텐츠 목록 조회 (커서 페이지네이션)
     * 스웨거의 typeEqual, keywordLike, tagsIn 등 쿼리 파라미터 반영
     */
    @GetMapping
    public CursorResponseContentDto getContents(
            @RequestParam(required = false) String typeEqual, // 콘텐츠 타입
            @RequestParam(required = false) String keywordLike, //검색 키워드
            @RequestParam(required = false) List<String> tagsIn, // 태그 목록
            @RequestParam(required = false) String cursor, // 커서
            @RequestParam(required = false) UUID idAfter, // 보조 커서
            @RequestParam int limit, // 한번에 가져올 개수
            @RequestParam String sortBy, // 정렬 기준
            @RequestParam SortDirection sortDirection // 정렬 방향
    ) {
        return contentService.getContents(typeEqual, keywordLike, tagsIn, cursor, idAfter, limit, sortBy, sortDirection);
    }

    /**
     * 콘텐츠 수정 (어드민)
     */
    @PatchMapping(value = "/{contentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ContentDto updateContent(
            @PathVariable UUID contentId,
            @RequestPart("request") @Valid ContentUpdateRequest request,
            @RequestPart(value = "thumbnail") MultipartFile thumbnail
    ) {
        return contentService.updateContent(contentId, request, thumbnail);
    }

    /**
     * 콘텐츠 삭제 (어드민)
     */
    @DeleteMapping("/{contentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteContent(@PathVariable UUID contentId) {
        contentService.deleteContent(contentId);
    }
}
