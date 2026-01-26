package com.sprint.batch.repository.contents;

import com.sprint.batch.entity.contents.Contents; // 배치 프로젝트의 엔티티
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContentsRepository extends JpaRepository<Contents, UUID> {

    // 배치에서 중복 체크를 위해 꼭 필요한 메서드만 남김
    boolean existsByTmdbId(Long tmdbId);
}