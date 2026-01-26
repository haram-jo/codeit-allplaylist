package com.sprint.batch.entity.contents; // 1. 현재 폴더 구조에 맞춘 패키지 경로

import jakarta.persistence.*; // 2. @Entity, @Id, @Column 등을 쓰기 위해 필수!
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)") // UUID를 DB에 효율적으로 저장하기 위해 추가 권장
    private UUID id;

    @Column(nullable = false, unique = true)
    private String tag; // 사람이 읽는 이름 (예: "액션", "스릴러")

    // 태그가 없을 경우 새롭게 생성하기 위한 생성자
    public Tag(String tag) {
        this.tag = tag;
    }
}