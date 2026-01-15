package com.sprint.api.repository.contents;

import com.sprint.api.dto.contents.SortDirection;
import com.sprint.api.entity.contents.Contents;

import java.util.List;
import java.util.UUID;

/**
 * Querydsl 전용 콘텐츠 조회 Repository 커스텀 인터페이스
 * - 복잡한 조건의 동적 쿼리 작성을 위해 별도의 커스텀 인터페이스로 분리
 */

public interface ContentsRepositoryCustom {
    List<Contents> findAllByCursor(
            String typeEqual,
            String keywordLike,
            List<String> tagsIn,
            String cursor,
            UUID idAfter,
            int limit,
            String sortBy,
            SortDirection sortDirection
    );
}
