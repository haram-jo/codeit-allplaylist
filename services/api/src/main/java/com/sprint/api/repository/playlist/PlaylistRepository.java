package com.sprint.api.repository.playlist;

import com.sprint.api.entity.playlists.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {
}
