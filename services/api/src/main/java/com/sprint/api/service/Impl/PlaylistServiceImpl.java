package com.sprint.api.service.Impl;

import com.sprint.api.dto.playlists.CursorResponsePlaylistDto;
import com.sprint.api.dto.playlists.PlaylistCreateRequest;
import com.sprint.api.dto.playlists.PlaylistDto;
import com.sprint.api.dto.playlists.PlaylistUpdateRequest;
import com.sprint.api.service.playlists.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** Test 위해 null 값 임시
  -반환하는 플레이리스트 서비스 구현체
 */

@Service
@RequiredArgsConstructor
@Transactional
public class PlaylistServiceImpl implements PlaylistService {

    // 1. 등록
    @Override
    public PlaylistDto createPlaylist(PlaylistCreateRequest request, UUID currentUserId) {
        System.out.println("서비스 진입 성공! 유저 ID: " + currentUserId);
        return null;
    }

    // 2. 단건 조회
    @Override
    @Transactional(readOnly = true)
    public PlaylistDto getPlaylist(UUID playlistId) {
        return null;
    }

    // 3. 수정
    @Override
    public PlaylistDto updatePlaylist(UUID playlistId, PlaylistUpdateRequest request, UUID currentUserId) {
        return null;
    }

    // 4. 목록 조회 (커서 페이징)
    @Override
    @Transactional(readOnly = true)
    public CursorResponsePlaylistDto getPlaylists(String keywordLike, UUID ownerIdEqual, UUID subscriberIdEqual,
                                                  String cursor, UUID idAfter, int limit,
                                                  String sortDirection, String sortBy) {
        return null;
    }

    // 5. 삭제
    @Override
    public void deletePlaylist(UUID playlistId, UUID currentUserId) {
    }
}