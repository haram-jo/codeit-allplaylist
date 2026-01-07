package com.sprint.api.dto.user;

import com.sprint.api.entity.user.UserRole;

public record UserRoleUpdateRequest (
    UserRole role
) {}

