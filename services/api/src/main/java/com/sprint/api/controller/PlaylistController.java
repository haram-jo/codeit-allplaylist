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
            @RequestParam(required = false) String keywordLike,
            @RequestParam(required = false) UUID ownerIdEqual,
            @RequestParam(required = false) UUID subscriberIdEqual,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) UUID idAfter,
            @RequestParam int limit,
            @RequestParam String sortDirection,
            @RequestParam String sortBy,
            @AuthenticationPrincipal CustomUserDetailsDto userDetails //여기에 있던 ')' 를 지우고
    ) {

        UUID currentUserId = userDetails.getUserId();

        CursorResponsePlaylistDto response = playlistService.getPlaylists(
                keywordLike,
                ownerIdEqual,
                currentUserId, // 현재 로그인한 내 ID를 넘김
                cursor,
                idAfter,
                limit,
                sortDirection,
                sortBy
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
    public ResponseEntity<PlaylistDto> getPlaylist(
            @PathVariable UUID playlistId,
            @AuthenticationPrincipal CustomUserDetailsDto userDetails // 1. 로그인 정보 추가
    ) {
        // 2. 서비스 호출 시 playlistId와 현재 유저의 ID를 함께 전달
        // (PlaylistService 인터페이스와 Impl에 파라미터를 추가한 것과 짝을 맞춰줍니다)
        PlaylistDto response = playlistService.getPlaylist(playlistId, userDetails.getUserId());

        return ResponseEntity.ok(response);
    }

    /**
     * 플레이리스트 수정
     */
    @PatchMapping("/{playlistId}")
    public ResponseEntity<PlaylistDto> updatePlaylist(
            @PathVariable UUID playlistId,
            @RequestBody PlaylistUpdateRequest request,   // 로그인된 유저 정보를 인자로 받음
            @AuthenticationPrincipal CustomUserDetailsDto userDetails
    ) {
        // 로그인한 유저 ID를 꺼냄
        UUID currentUserId = userDetails.getUserId();

        PlaylistDto response = playlistService.updatePlaylist(playlistId, request, currentUserId);
        return ResponseEntity.ok(response);
    }

    /**
     * 플레이리스트 삭제
     */
    @DeleteMapping("/{playlistId}")
    public ResponseEntity<Void> deletePlaylist(
            @PathVariable UUID playlistId,
            @AuthenticationPrincipal CustomUserDetailsDto userDetails // 로그인된 유저 정보를 인자로 받음
    ) {
        // 로그인한 유저 ID를 꺼냄
        UUID currentUserId = userDetails.getUserId();

        playlistService.deletePlaylist(playlistId, currentUserId);

        return ResponseEntity.noContent().build();
    }

    /**
     * 플레이리스트 구독 (등록)
     */
    @PostMapping("/{playlistId}/subscription")
    public ResponseEntity<Void> createPlaylistSubscription(
            @PathVariable UUID playlistId,
            @AuthenticationPrincipal CustomUserDetailsDto userDetails
    ) {
        playlistService.createPlaylistSubscription(playlistId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    /**
     * 플레이리스트 구독 취소 (삭제)
     */
    @DeleteMapping("/{playlistId}/subscription")
    public ResponseEntity<Void> deletePlaylistSubscription(
            @PathVariable UUID playlistId,
            @AuthenticationPrincipal CustomUserDetailsDto userDetails
    ) {
        playlistService.deletePlaylistSubscription(playlistId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    /**
     * 플레이리스트에 콘텐츠 추가 (등록)
     * - 어떤 플레이리스트에 어떤 콘텐츠를 넣을 것인가
     */
    @PostMapping("/{playlistId}/contents/{contentId}")
    public ResponseEntity<Void> createPlaylistContent(
            @PathVariable UUID playlistId,
            @PathVariable UUID contentId,
            @AuthenticationPrincipal CustomUserDetailsDto userDetails
    ) {
        playlistService.createPlaylistContent(playlistId, contentId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    /**
     * 플레이리스트에서 콘텐츠 삭제
     */
    @DeleteMapping("/{playlistId}/contents/{contentId}")
    public ResponseEntity<Void> deletePlaylistContent(
            @PathVariable UUID playlistId,
            @PathVariable UUID contentId,
            @AuthenticationPrincipal CustomUserDetailsDto userDetails
    ) {
        playlistService.deletePlaylistContent(playlistId, contentId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }
}




