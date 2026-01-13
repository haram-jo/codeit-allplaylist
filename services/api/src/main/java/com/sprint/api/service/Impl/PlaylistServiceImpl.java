package com.sprint.api.service.Impl;

import com.sprint.api.dto.playlists.CursorResponsePlaylistDto;
import com.sprint.api.dto.playlists.PlaylistCreateRequest;
import com.sprint.api.dto.playlists.PlaylistDto;
import com.sprint.api.dto.playlists.PlaylistUpdateRequest;
import com.sprint.api.dto.user.UserSummary;
import com.sprint.api.entity.playlists.Playlist;
import com.sprint.api.entity.user.User;
import com.sprint.api.repository.playlist.PlaylistRepository;
import com.sprint.api.repository.user.UserRepository;
import com.sprint.api.service.playlists.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/** 플레이리스트 서비스 구현체
   - 플레이리스트를 생성, 조회, 수정, 삭제하는 기능을 제공
* */

@Service
@RequiredArgsConstructor
@Transactional
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;

    /** 1. 생성
     - 사용자의 UUID를 통해 유저를 찾고,
     - 플레이리스트를 저장한뒤
     - PlaylistDto 구조에 맞춰 결과를 반환
     */
    @Override
    @Transactional
    public PlaylistDto createPlaylist(PlaylistCreateRequest request, UUID currentUserId) {

        User user = userRepository.findById(currentUserId.toString())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // Playlist 엔티티 생성
        Playlist playlist = Playlist.builder()
                .title(request.title())
                .description(request.description())
                .user(user)
                .subscriberCount(0L)
                .build();

        // DB 저장
        Playlist savedPlaylist = playlistRepository.save(playlist);

        // DTO로 변환하여 반환
        return convertToDto(savedPlaylist);
    }

    /**
     * 엔티티 -> DTO 변환, DTO에 적어도 되고, Impl에 적어도 됨
     */
    private PlaylistDto convertToDto(Playlist playlist) {
        // UserSummary 생성
        UserSummary owner = new UserSummary(
                UUID.fromString(playlist.getUser().getId()),
                playlist.getUser().getName(),
                playlist.getUser().getProfileImageUrl()
        );

        // PlaylistDto 생성
        return new PlaylistDto(
                playlist.getId(),
                owner,
                playlist.getTitle(),
                playlist.getDescription(),
                playlist.getUpdatedAt(),
                playlist.getSubscriberCount(),
                false,   //지금 로그인한 내가 이걸 구독 중인지
                List.of() // contents (초기값)
        );
    }

    /** 2. 단건조회
     * - 플레이리스트 ID로 플레이리스트를 조회하고,
     * - PlaylistDto 구조에 맞춰 결과를 반환
     */
    @Override
    @Transactional(readOnly = true)
    public PlaylistDto getPlaylist(UUID playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(()-> new IllegalArgumentException("플레이리스트를 찾을 수 없습니다."));
        return convertToDto(playlist);
    }

    /** 3. 수정
     - 플레이리스트 ID로 플레이리스트를 조회하고,
     - 현재 사용자가 작성자인지 확인한 뒤 플레이리스트 정보 업데이트,
     - PlaylistDto 구조에 맞춰 결과를 반환
     */
    @Override
    @Transactional
    public PlaylistDto updatePlaylist(UUID playlistId, PlaylistUpdateRequest request, UUID currentUserId) {

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 플레이리스트입니다."));

        if (!playlist.getUser().getId().equals(currentUserId.toString())) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }
        playlist.update(request.title(), request.description());
        return convertToDto(playlist);
    }

    /** 4. 삭제
     - 플레이리스트 ID로 플레이리스트를 조회하고,
     - 현재 사용자가 작성자인지 확인한 뒤 플레이리스트 삭제
     */
    @Override
    @Transactional
    public void deletePlaylist(UUID playlistId, UUID currentUserId) {

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 플레이리스트입니다."));

        if (!playlist.getUser().getId().equals(currentUserId.toString())) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        playlistRepository.delete(playlist);
    }

    /** 5. 목록조회
     - 플레이리스트 목록을 조회하는 메서드 (미구현)
     */
    @Override
    @Transactional(readOnly = true)
    public CursorResponsePlaylistDto getPlaylists(String keywordLike, UUID ownerIdEqual, UUID subscriberIdEqual,
                                                  String cursor, UUID idAfter, int limit,
                                                  String sortDirection, String sortBy) {
        return null;
    }
}