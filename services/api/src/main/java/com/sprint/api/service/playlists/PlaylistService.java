package com.sprint.api.service.playlists;

import com.sprint.api.dto.playlists.CursorResponsePlaylistDto;
import com.sprint.api.dto.playlists.PlaylistCreateRequest;
import com.sprint.api.dto.playlists.PlaylistDto;
import com.sprint.api.dto.playlists.PlaylistUpdateRequest;
import jakarta.validation.Valid;

import java.util.UUID;

public interface PlaylistService {

    PlaylistDto createPlaylist(@Valid PlaylistCreateRequest request, UUID currentUserId);

    PlaylistDto getPlaylist(UUID playlistId);

    PlaylistDto updatePlaylist(UUID playlistId, PlaylistUpdateRequest request, UUID currentUserId);

    CursorResponsePlaylistDto getPlaylists(String keywordLike, UUID ownerIdEqual, UUID subscriberIdEqual, String cursor, UUID idAfter, int limit, String sortDirection, String sortBy);

    void deletePlaylist(UUID playlistId, UUID currentUserId);

    // 플레이리스트 구독
    void createPlaylistSubscription(UUID playlistId, UUID userId);

    // 플레이리스트 구독 취소
    void deletePlaylistSubscription(UUID playlistId, UUID userId);

    //플레이리스트 콘텐츠 추가
    void createPlaylistContent(UUID playlistId, UUID contentId, UUID userId);

    // 플레이리스트 콘텐츠 삭제
    void deletePlaylistContent(UUID playlistId, UUID contentId, UUID userId);
}

