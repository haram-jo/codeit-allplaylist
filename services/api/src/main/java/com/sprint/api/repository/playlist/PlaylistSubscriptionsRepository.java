package com.sprint.api.repository.playlist;

import com.sprint.api.entity.playlists.PlaylistSubscriptions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/** 플레이리스트 구독 정보 Repository
 */

public interface PlaylistSubscriptionsRepository extends JpaRepository<PlaylistSubscriptions, UUID> {
    boolean existsByPlaylistIdAndUserId(UUID playlistId, String userId);
    Optional<PlaylistSubscriptions> findByPlaylistIdAndUserId(UUID playlistId, String userId);
}