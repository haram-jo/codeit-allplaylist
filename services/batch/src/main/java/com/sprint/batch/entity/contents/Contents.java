package com.sprint.batch.entity.contents; // 1. 사진 속 구조에 맞춘 패키지 경로

import com.sprint.batch.common.BaseEntity; // 2. api -> batch 내의 경로로 수정
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
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "average_rating")
    private Integer averageRating;

    @Column(name = "review_count")
    private Integer reviewCount;

    @Column(name = "watcher_count")
    @Builder.Default
    private Long watcherCount = 0L;

    @Column(name = "tmdb_id", unique = true)
    private Long tmdbId;

    @Builder.Default
    @OneToMany(mappedBy = "contents", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContentTag> contentTags = new ArrayList<>();

    public void addTag(ContentTag contentTag) {
        this.contentTags.add(contentTag);
    }
}