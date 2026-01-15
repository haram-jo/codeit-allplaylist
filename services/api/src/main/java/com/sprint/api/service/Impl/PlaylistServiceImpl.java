package com.sprint.api.service.Impl;

import com.sprint.api.common.exception.CustomException;
import com.sprint.api.common.exception.ErrorCode;
import com.sprint.api.dto.notifications.NotificationLevel;
import com.sprint.api.dto.playlists.CursorResponsePlaylistDto;
import com.sprint.api.dto.playlists.PlaylistCreateRequest;
import com.sprint.api.dto.playlists.PlaylistDto;
import com.sprint.api.dto.playlists.PlaylistUpdateRequest;
import com.sprint.api.dto.user.UserSummary;
import com.sprint.api.entity.contents.Contents;
import com.sprint.api.entity.playlists.Playlist;
import com.sprint.api.entity.playlists.PlaylistContents;
import com.sprint.api.entity.playlists.PlaylistSubscriptions;
import com.sprint.api.entity.user.User;
import com.sprint.api.repository.contents.ContentsRepository;
import com.sprint.api.repository.playlist.PlaylistContentsRepository;
import com.sprint.api.repository.playlist.PlaylistRepository;
import com.sprint.api.repository.playlist.PlaylistSubscriptionsRepository;
import com.sprint.api.repository.user.UserRepository;
import com.sprint.api.service.notification.NotificationService;
import com.sprint.api.service.playlists.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/** 플레이리스트 서비스 구현체
   - 플레이리스트 CRUD
   - 플레이리스트 구독, 구독 취소, 콘텐츠 추가, 삭제
* */

@Service
@Transactional
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final PlaylistSubscriptionsRepository subscriptionsRepository;
    private final PlaylistContentsRepository playlistContentsRepository;
    private final ContentsRepository contentsRepository;
    private final NotificationService notificationService;

    /**
     * 1. 생성
     * - 사용자의 UUID를 통해 유저를 찾고,
     * - 플레이리스트를 저장한뒤
     * - PlaylistDto 구조에 맞춰 결과를 반환
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

    /**
     * 2. 단건조회
     * - 플레이리스트 ID로 플레이리스트를 조회하고,
     * - PlaylistDto 구조에 맞춰 결과를 반환
     */
    @Override
    @Transactional(readOnly = true)
    public PlaylistDto getPlaylist(UUID playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("플레이리스트를 찾을 수 없습니다."));
        return convertToDto(playlist);
    }

    /**
     * 3. 수정
     * - 플레이리스트 ID로 플레이리스트를 조회하고,
     * - 현재 사용자가 작성자인지 확인한 뒤 플레이리스트 정보 업데이트,
     * - PlaylistDto 구조에 맞춰 결과를 반환
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

    /**
     * 4. 삭제
     * - 플레이리스트 ID로 플레이리스트를 조회하고,
     * - 현재 사용자가 작성자인지 확인한 뒤 플레이리스트 삭제
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

    /**
     * 5. 목록조회
     * - 플레이리스트 목록을 조회
     * - 커서 기반 페이징 처리
     */
    @Override
    @Transactional(readOnly = true)
    public CursorResponsePlaylistDto getPlaylists(String keywordLike, UUID ownerIdEqual, UUID subscriberIdEqual,
                                                  String cursor, UUID idAfter, int limit,
                                                  String sortDirection, String sortBy) {

        // DB에서 limit + 1개를 조회
        List<Playlist> entities = playlistRepository.findAllByCursor(
                keywordLike, ownerIdEqual, subscriberIdEqual,
                cursor, idAfter, limit,
                sortDirection, sortBy);

        // 다음 페이지(hasNext) 판단
        boolean hasNext = entities.size() > limit;
        List<Playlist> resultData = hasNext ? entities.subList(0, limit) : entities;

        // Entity -> DTO 변환
        List<PlaylistDto> data = resultData.stream()
                .map(this::convertToDto)
                .toList();

        // 다음 페이지 요청을 위한 커서(nextCursor, nextIdAfter) 생성
        String nextCursor = null;
        UUID nextIdAfter = null;

        if (hasNext && !resultData.isEmpty()) {
            Playlist lastItem = resultData.get(resultData.size() - 1);

            // 정렬조건: 최신순, 구독순
            nextCursor = switch (sortBy) {
                case "subscribeCount" -> String.valueOf(lastItem.getSubscriberCount());
                default -> lastItem.getUpdatedAt().toString(); // 최신순
            };

            nextIdAfter = lastItem.getId();
        }

        // 전체 개수 조회
        long totalCount = playlistRepository.countByConditions(keywordLike, ownerIdEqual, subscriberIdEqual);

        // DTO의 enum 타입에 맞춰 변환
        CursorResponsePlaylistDto.SortDirection direction =
                sortDirection.equalsIgnoreCase("ASCENDING") ?
                        CursorResponsePlaylistDto.SortDirection.ASCENDING :
                        CursorResponsePlaylistDto.SortDirection.DESCENDING;

        return new CursorResponsePlaylistDto(
                data,
                nextCursor,
                nextIdAfter,
                hasNext,
                totalCount,
                sortBy,
                direction
        );
    }


    //========= 플레이리스트 구독 및 콘텐츠 관리 ========= //

    /**
     * 6. 플레이리스트 구독 (등록)
     * - param playlistId
     * - param userId
     */
    @Override
    @Transactional
    public void createPlaylistSubscription(UUID playlistId, UUID userId) {
        // 1. 중복 구독 체크
        if (subscriptionsRepository.existsByPlaylistIdAndUserId(playlistId, userId.toString())) {
            throw new CustomException(ErrorCode.ALREADY_SUBSCRIBED);
        }

        // 2. 플레이리스트 조회
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAYLIST_NOT_FOUND));

        // 3. 유저 조회
        User user = userRepository.findById(userId.toString())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 4. 구독 정보 저장 (userId 대신 user 객체를 전달)
        PlaylistSubscriptions subscription = PlaylistSubscriptions.builder()
                .playlist(playlist)
                .user(user) // 엔티티 필드명에 맞게 .user() 호출
                .build();

        subscriptionsRepository.save(subscription);

        // 5. 구독자 수 증가
        playlist.increaseSubscriberCount();

        // 6. 실시간 알림 발송 추가
        // 플레이리스트 주인(playlist.getUser())에게 알림을 보냅니다.
        String receiverId = playlist.getUser().getId();
        String title = "새로운 구독자!";
        String content = user.getName() + "님이 당신의 [" + playlist.getTitle() + "] 플리를 구독했습니다.";

        notificationService.createNotification(
                receiverId,
                title,
                content,
                NotificationLevel.INFO
        );
    }

    /**
     * 7. 플레이리스트 구독취소
     * - param playlistId
     * - param userId
     *
     */
    @Override
    @Transactional
    public void deletePlaylistSubscription(UUID playlistId, UUID userId) {
        // 1. 구독 정보 조회
        PlaylistSubscriptions subscription = subscriptionsRepository.findByPlaylistIdAndUserId(playlistId, userId.toString())
                .orElseThrow(() -> new CustomException(ErrorCode.SUBSCRIPTION_NOT_FOUND)); // 구독 중이 아닐 때 에러

        // 2. 플레이리스트 조회 (구독자 수 감소를 위해)
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAYLIST_NOT_FOUND));

        // 3. 삭제 및 구독자 수 감소
        subscriptionsRepository.delete(subscription);
        playlist.decreaseSubscriberCount();
    }

    /**
     * 8. 플레이리스트 콘텐츠 추가
     * - param playlistId
     * - param contentId
     * - param userId
     *
     */
    @Override
    @Transactional
    public void createPlaylistContent(UUID playlistId, UUID contentId, UUID userId) { //엔티티 필드보고 타입 판단!
        // 1. 플레이리스트 존재 여부 및 소유권 확인
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAYLIST_NOT_FOUND));

        // 플레이리스트 소유권 확인 (내 플리인지 체크)
        if (!playlist.getUser().getId().equals(userId.toString())) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        // 2. 콘텐츠 존재 여부 확인
        Contents content = contentsRepository.findById(contentId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));

        // 3. 중복 추가 방지
        if (playlistContentsRepository.existsByPlaylistIdAndContentId(playlistId, contentId)) {
            throw new CustomException(ErrorCode.ALREADY_ADDED_CONTENT);
        }

        // 4. 저장
        PlaylistContents playlistContents = PlaylistContents.builder()
                .playlist(playlist)
                .content(content)
                .build();

        playlistContentsRepository.save(playlistContents);
    }

    /**
     * 9. 플레이리스트 콘텐츠 삭제
     * - param playlistId
     * - param contentId
     * - param userId
     *
     */
    @Override
    @Transactional
    public void deletePlaylistContent(UUID playlistId, UUID contentId, UUID userId) {

        // 소유권 확인 (내 플리인지)
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAYLIST_NOT_FOUND));

        // .toString()을 사용하여 비교
        if (!playlist.getUser().getId().equals(userId.toString())) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        // 중간 테이블에서 데이터 찾아 삭제
        PlaylistContents pc = playlistContentsRepository.findByPlaylistIdAndContentId(playlistId, contentId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));

        playlistContentsRepository.delete(pc);
    }
}