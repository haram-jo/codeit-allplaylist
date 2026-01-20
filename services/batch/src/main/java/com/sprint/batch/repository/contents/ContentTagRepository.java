package com.sprint.batch.repository.contents;

import com.sprint.batch.entity.contents.ContentTag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ContentTagRepository extends JpaRepository<ContentTag, UUID> {
}