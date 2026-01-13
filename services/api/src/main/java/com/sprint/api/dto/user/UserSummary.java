package com.sprint.api.dto.user;

import java.util.UUID;

/* 소유자(owner) 정보 담는 용도
 */
public record UserSummary(
        UUID userId,
        String name,
        String profileImageUrl
) {}
