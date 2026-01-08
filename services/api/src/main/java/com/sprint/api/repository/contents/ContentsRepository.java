package com.sprint.api.repository.contents;

import com.sprint.api.dto.contents.SortDirection;
import com.sprint.api.entity.contents.Contents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContentsRepository extends JpaRepository<Contents, UUID>, ContentsRepositoryCustom {
}
