package com.sprint.api.repository.contents;

import com.sprint.api.entity.contents.Contents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContentsRepository extends JpaRepository<Contents, UUID> {
// 페이징을 위한 복잡한 쿼리는 추후 Querydsl로 추가
}
