package com.sprint.api.dto.user;

import com.sprint.api.entity.user.UserRole;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserDto (

    String id,
    LocalDateTime createdAt,
    String email,
    String name,
    String profileImageUrl,
    UserRole role,
    boolean locked
) {}
