package com.sprint.api.repository.contents;

import com.sprint.api.dto.contents.SortDirection;
import com.sprint.api.entity.contents.Contents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContentsRepository extends JpaRepository<Contents, UUID>, ContentsRepositoryCustom {

    // Cotents가 UUID 밖에 없어서 중복 데이터가 DB에 적재될 수 있기에,
    // TMDB ID로 이미 존재하는 콘텐츠인지 확인하기 위한 메서드
    boolean existsByTmdbId(Long tmdbId);
}
