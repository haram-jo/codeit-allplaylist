package com.sprint.api.dto;

import com.sprint.api.entity.UserRole;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UserDto (

    UUID id,
    OffsetDateTime createdAt, // 날짜+시간+시차
    String email,
    String name,
    String profileImageUrl,
    UserRole role,
    boolean locked
) {}
