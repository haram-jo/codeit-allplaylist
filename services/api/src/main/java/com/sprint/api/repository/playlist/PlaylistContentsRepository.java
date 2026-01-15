package com.sprint.api.repository.playlist;

import com.sprint.api.entity.playlists.PlaylistContents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/** 플레이리스트 내 콘텐츠 관리 Repository
 */

public interface PlaylistContentsRepository extends JpaRepository<PlaylistContents, UUID> {
    boolean existsByPlaylistIdAndContentId(UUID playlistId, UUID contentId);
    Optional<PlaylistContents> findByPlaylistIdAndContentId(UUID playlistId, UUID contentId);
}