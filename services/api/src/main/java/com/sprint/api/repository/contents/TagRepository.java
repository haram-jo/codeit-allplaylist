package com.sprint.api.repository.contents;

import com.sprint.api.entity.contents.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {
    // 태그 이름으로 기존에 저장된 태그가 있는지 확인하기 위해 필요
    Optional<Tag> findByTag(String tag);
}
