package com.sprint.api.service.Impl;

import com.sprint.api.dto.contents.*;
import com.sprint.api.entity.contents.ContentTag;
import com.sprint.api.entity.contents.Contents;
import com.sprint.api.entity.contents.Tag;
import com.sprint.api.repository.contents.ContentTagRepository;
import com.sprint.api.repository.contents.ContentsRepository;
import com.sprint.api.repository.contents.ContentsRepositoryCustom;
import com.sprint.api.repository.contents.TagRepository;
import com.sprint.api.service.contents.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor // 필수 생성자 필드 자동 생성
@Transactional(readOnly = true)
public class ContentServiceImpl implements ContentService {

    private final ContentsRepository contentsRepository;
    private final TagRepository tagRepository;
    private final ContentTagRepository contentTagRepository;
    private final String uploadPath = "uploads/thumbnails";

    /**
     * 콘텐츠 생성 (등록) 로직
     */
    @Override
    @Transactional
    public ContentDto createContent(ContentCreateRequest request, MultipartFile thumbnail) {

        // 1. 썸네일 주소를 담을 변수 (사진이 없으면 null)
        String thumbnailUrl = null;

        // 2. 사진이 있을 경우
        if (thumbnail != null && !thumbnail.isEmpty()) {
            // 아래에 만든 saveThumbnail() 메서드를 호출해서 사진을 폴더에 저장하고 주소를 가져옴
            thumbnailUrl = saveThumbnail(thumbnail);
        }

        // 3. 사용자가 입력한 정보(DTO)를 바탕으로 DB에 저장할 엔티티(Entity) 객체를 만듦
        Contents contents = Contents.builder()
                .type(request.type())
                .title(request.title())
                .description(request.description())
                .thumbnailUrl(thumbnailUrl) // 저장된 사진 주소 혹은 null
                .averageRating(0)    // Double 타입에 맞춰 0.0으로 설정
                .reviewCount(0)        // 초기 리뷰 수 0
                .watcherCount(0L)      // 추가된 필드: 초기 시청자 수 0 (Long 타입이라 0L)
                .build();

        // 4. 콘텐츠 기본 정보를 DB에 저장
        Contents savedContents = contentsRepository.save(contents);

        // 5. 태그(Tag) 처리 로직
        if (request.tags() != null) {
            for (String tagName : request.tags()) {
                // (1) DB에 이미 이 이름의 태그가 있는지 확인하고, 없으면 새로 저장
                Tag tag = tagRepository.findByTag(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));

                // (2) 콘텐츠와 태그를 연결해주는 중간 테이블(ContentTag) 객체를 만듦
                ContentTag contentTag = ContentTag.builder()
                        .contents(savedContents)
                        .tag(tag)
                        .build();

                // (3) 콘텐츠 객체 안의 리스트에 이 연결 정보 추가
                savedContents.addTag(contentTag);
            }
        }

        // 6. DB에 저장된 정보를 다시 사용자에게 보여줄 수 있도록 DTO로 변환해서 반환
        return convertToDto(savedContents);
    }

    /**
     * 실제 파일을 하드디스크 폴더에 저장하는 메서드
     */
    private String saveThumbnail(MultipartFile file) {
        try {
            // 1. 저장할 폴더의 경로 객체 만듦 (uploads/thumbnails)
            Path root = Paths.get(uploadPath);

            // 2. 해당 폴더가 내 컴퓨터에 없으면 자동으로 생성
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            // 3. 파일 이름이 겹치지 않게 '랜덤ID_원래이름' 형태
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            // 4. 저장될 '폴더 위치 + 파일명' 경로를 만듦
            Path targetPath = root.resolve(fileName);

            // 5. 들어온 사진 파일의 데이터를 목적지(targetPath)로 복사해서 물리적으로 저장
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 6. 나중에 웹 브라우저에서 이 사진을 보여줄 때 사용할 가상 주소를 반환
            // (예: /images/abc-123-photo.jpg)
            return "/images/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("썸네일 파일 저장 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * DB에서 꺼낸 엔티티(Entity) -> DTO 변환 메서드
     */
    private ContentDto convertToDto(Contents contents) {
        // 태그 이름 리스트
        List<String> tagList = contents.getContentTags().stream()
                .map(ct -> ct.getTag().getTag())
                .toList();

        return new ContentDto(
                contents.getId(),
                contents.getType(),
                contents.getTitle(),
                contents.getDescription(),
                contents.getThumbnailUrl(),
                tagList,
                contents.getAverageRating(),
                contents.getReviewCount(),
                contents.getWatcherCount()
        );
    }

    /**
     * 콘텐츠 단건 상세 조회
     */
    @Override
    public ContentDto getContent(UUID contentId) {
        Contents contents = contentsRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 콘텐츠를 찾을 수 없습니다."));
        return convertToDto(contents);
    }

    /**
     * 콘텐츠 삭제
     */
    @Override
    @Transactional
    public void deleteContent(UUID contentId) {
        contentsRepository.deleteById(contentId);

    }

    /** 콘텐츠 수정
     *  - 썸네일 새로 업로드시 교체, 없으면 기존 것 유지
     *  - 태그 수정시 clear() 후 재연결
     *  - 위와 같은 방법이 코드가 단순해지고 버그 발생 가능성이 적음
     */
    @Override
    @Transactional
    public ContentDto updateContent(UUID contentId, ContentUpdateRequest request, MultipartFile thumbnail) {
        //1. 기존 데이터 조회 (영속성 컨텍스트에 올라감)
        Contents contents = contentsRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 콘텐츠를 찾을 수 없습니다." + contentId));

        //2. 일반 필드 수정 (제목, 설명)
        contents.update(request.title(), request.description());

        // 3. 썸네일 수정 (파일이 넘어왔을 때만 교체)
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String newThumbnailUrl = saveThumbnail(thumbnail);
            contents.updateThumbnail(newThumbnailUrl);
        }

        // 2. 태그 수정 (clear 사용)
        if (request.tags() != null) {
            // 리스트를 비우면 orphanRemoval = true 설정에 의해 DB 데이터도 삭제됨
            contents.getContentTags().clear();

            for (String tagName : request.tags()) {
                Tag tag = tagRepository.findByTag(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));

                ContentTag contentTag = ContentTag.builder()
                        .contents(contents)
                        .tag(tag)
                        .build();

                contents.addTag(contentTag);
            }
        }
        return convertToDto(contents);
    }


    /**
     * 콘텐츠 목록 조회 (커서 페이지네이션)
     */
    @Override
    public CursorResponseContentDto getContents(String typeEqual, String keywordLike, List<String> tagsIn,
                                                String cursor, UUID idAfter, int limit,
                                                String sortBy, SortDirection sortDirection) {

        // 1. DB에서 limit + 1개를 조회 (다음 페이지 존재 여부 확인용)
        List<Contents> entities = contentsRepository.findAllByCursor(
                typeEqual, keywordLike, tagsIn, cursor, idAfter, limit, sortBy, sortDirection);

        // 2. 다음 페이지(hasNext) 판단 및 실제 데이터(limit개) 절삭
        boolean hasNext = entities.size() > limit;
        List<Contents> resultData = hasNext ? entities.subList(0, limit) : entities;

        // 3. Entity -> DTO 변환
        List<ContentDto> data = resultData.stream()
                .map(this::convertToDto)
                .toList();

        // 4. 다음 페이지 요청을 위한 커서(nextCursor, nextIdAfter) 생성
        String nextCursor = null;
        UUID nextIdAfter = null;

        if (hasNext && !resultData.isEmpty()) {
            Contents lastItem = resultData.get(resultData.size() - 1);

            // 정렬 기준(sortBy)에 맞는 값을 문자열로 변환하여 nextCursor에 담음
            nextCursor = switch (sortBy) {
                case "watcherCount" -> String.valueOf(lastItem.getWatcherCount());
                case "averageRating", "rate" -> String.valueOf(lastItem.getAverageRating()); // Swagger의 rate와 매핑
                default -> lastItem.getCreatedAt().toString(); // 최신순
            };

            // 보조 커서로 사용될 마지막 아이템의 ID
            nextIdAfter = lastItem.getId();
        }

        // 5. 전체 개수 조회
        long totalCount = contentsRepository.count(); // 전체 콘텐츠 개수

        return CursorResponseContentDto.builder()
                .data(data)
                .nextCursor(nextCursor)
                .nextIdAfter(nextIdAfter)
                .hasNext(hasNext)
                .totalCount(totalCount)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
    }
}