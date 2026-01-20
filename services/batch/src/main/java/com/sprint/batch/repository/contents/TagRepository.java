package com.sprint.batch.repository.contents;

import com.sprint.batch.entity.contents.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {
    Optional<Tag> findByTag(String tag);
}