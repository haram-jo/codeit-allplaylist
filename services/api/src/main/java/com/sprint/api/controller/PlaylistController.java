package com.sprint.api.controller;

import com.sprint.api.dto.playlists.CursorResponsePlaylistDto;
import com.sprint.api.dto.playlists.PlaylistCreateRequest;
import com.sprint.api.dto.playlists.PlaylistDto;
import com.sprint.api.dto.playlists.PlaylistUpdateRequest;
import com.sprint.api.dto.user.CustomUserDetailsDto;
import com.sprint.api.service.playlists.PlaylistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    /**
     * 플레이리스트 목록 조회 (커서 페이지네이션)
     */
    @GetMapping
    public ResponseEntity<CursorResponsePlaylistDto> getPlaylists(
            @RequestParam(required = false) String keywordLike, // 검색 키워드
            @RequestParam(required = false) UUID ownerIdEqual, // 소유자 ID
            @RequestParam(required = false) UUID subscriberIdEqual, // 구독자 ID
            @RequestParam(required = false) String cursor, // 커서
            @RequestParam(required = false) UUID idAfter, // ID 이후
            @RequestParam int limit, // 필수값, 한번에 가져올 개수
            @RequestParam String sortDirection, // 필수값, 정렬 방향
            @RequestParam String sortBy // 필수값, 정렬 기준
    ) {
        // 서비스 메소드명을 규칙에 따라 getPlaylists로 설정
        CursorResponsePlaylistDto response = playlistService.getPlaylists(
                keywordLike, ownerIdEqual, subscriberIdEqual, cursor, idAfter, limit, sortDirection, sortBy
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 플레이리스트 생성
     */
    @PostMapping
    public ResponseEntity<PlaylistDto> createPlaylist(
            @Valid @RequestBody PlaylistCreateRequest request,
            @AuthenticationPrincipal CustomUserDetailsDto userDetails
    ) {
        // 1. UUID가 필요할 때
        UUID userId = userDetails.getUserId();

        // 2. 이메일이 필요할 때
        String email = userDetails.getEmail();

        // userDetails에서 UUID userId를 꺼내서 서비스로 전달
        PlaylistDto response = playlistService.createPlaylist(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 플레이리스트 단건 조회
     */
    @GetMapping("/{playlistId}")
    public ResponseEntity<PlaylistDto> getPlaylist(@PathVariable UUID playlistId) {
        PlaylistDto response = playlistService.getPlaylist(playlistId);
        return ResponseEntity.ok(response);
    }

    /**
     * 플레이리스트 수정
     */
    @PatchMapping("/{playlistId}")
    public ResponseEntity<PlaylistDto> updatePlaylist(
            @PathVariable UUID playlistId,
            @RequestBody PlaylistUpdateRequest request
    ) {
        // 소유자 권한 체크 로직은 서비스 계층에서 처리 권장
        UUID currentUserId = UUID.randomUUID();
        PlaylistDto response = playlistService.updatePlaylist(playlistId, request, currentUserId);
        return ResponseEntity.ok(response);
    }

    /**
     * 플레이리스트 삭제
     */
    @DeleteMapping("/{playlistId}")
    public ResponseEntity<Void> deletePlaylist(
            @PathVariable UUID playlistId
    ) {
        UUID currentUserId = UUID.randomUUID();
        playlistService.deletePlaylist(playlistId, currentUserId);
        return ResponseEntity.ok().build();
    }
}




