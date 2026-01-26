package com.sprint.batch.entity.contents; // 1. 사진 속 구조에 맞춘 패키지 경로

import jakarta.persistence.*; // 2. @Entity, @Id 등을 사용하기 위한 라이브러리
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "content_tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentTag {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)") // UUID를 이진값으로 저장하기 위해 추가 권장
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contents_id")
    private Contents contents; // 같은 패키지에 있으므로 import 불필요

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag; // 같은 패키지에 있으므로 import 불필요

    //콘텐츠와 태그를 이어주는 생성자
    @Builder
    public ContentTag(Contents contents, Tag tag) {
        this.contents = contents;
        this.tag = tag;
    }
}