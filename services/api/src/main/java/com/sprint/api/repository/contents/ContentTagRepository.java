package com.sprint.api.repository.contents;

import com.sprint.api.entity.contents.ContentTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContentTagRepository extends JpaRepository<ContentTag, UUID> {
}
