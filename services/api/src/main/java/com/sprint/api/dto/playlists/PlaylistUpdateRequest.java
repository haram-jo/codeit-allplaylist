package com.sprint.api.dto.playlists;

public record PlaylistUpdateRequest(
    String title,
    String description
) {}
