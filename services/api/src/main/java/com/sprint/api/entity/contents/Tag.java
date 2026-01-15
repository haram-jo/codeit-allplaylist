package com.sprint.api.entity.contents;

import jakarta.persistence.*;
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
    private UUID id;

    @Column(nullable = false, unique = true)
    private String tag; // 사람이 읽는 이름 (예: "액션", "스릴러")

    // 태그가 없을 경우 ServiceImpl에서 new Tag(tagName)하기 위한 메서드
    public Tag(String tag) {
        this.tag = tag;
    }
}

