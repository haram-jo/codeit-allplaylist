package com.sprint.api.entity.contents;

import com.sprint.api.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "contents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Contents extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)") // DB 저장 시 16바이트 이진값으로 효율적 저장
    private UUID id; // 아이디 (PK)

    @Column(nullable = false)
    private String type; // 콘텐츠 타입

    @Column(nullable = false)
    private String title; // 제목

    @Column(name = "description", nullable = false)
    private String description; // 설명

    @Column(name = "thumbnail_url")
    private String thumbnailUrl; // 썸네일 url

    @Column(name = "average_rating")
    private Integer averageRating; // 평점 (집계된 평균값)

    @Column(name = "review_count")
    private Integer reviewCount; // 리뷰수 (필터링할 때 인기순)

    @Column(name = "watcher_count")
    @Builder.Default // 값 따로 없으면 기본값 0주입
    private Long watcherCount = 0L;

    @Column(name = "tmdb_id", unique = true) // Open APi 끌어올때 콘텐츠 중복 방지를 위해 추가
    private Long tmdbId;

    // 태그와의 연관 관계
    @Builder.Default
    @OneToMany(mappedBy = "contents", cascade = CascadeType.ALL, orphanRemoval = true) // 컨텐츠가 PK 주인
    private List<ContentTag> contentTags = new ArrayList<>();

    // 콘텐츠에 태그 추가할때 쓰는 메서드
    public void addTag(ContentTag contentTag) {
        this.contentTags.add(contentTag);
    }

    // 콘텐츠 제목, 설명 수정 메서드
    public void update(String title, String description) {
        // 들어온 값이 null이 아닐 때만 필드를 교체 (null이면 기존 값 유지)
        if (title != null) {
            this.title = title;
        }
        if (description != null) {
            this.description = description;
        }
    }

    // 콘텐츠 썸네일 수정 메서드
    public void updateThumbnail(String newThumbnailUrl) {
        if (newThumbnailUrl != null) {
            this.thumbnailUrl = newThumbnailUrl;
        }
    }
}
