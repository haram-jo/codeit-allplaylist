package com.sprint.api.dto.user;

import com.sprint.api.entity.user.UserRole;
import java.time.LocalDateTime;

public record UserDto (

    String id,
    LocalDateTime createdAt,
    String email,
    String name,
    String profileImageUrl,
    UserRole role,
    boolean locked
) {}
