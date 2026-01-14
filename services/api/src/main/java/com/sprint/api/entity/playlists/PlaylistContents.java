package com.sprint.api.entity.playlists;

import com.sprint.api.entity.contents.Contents;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

/**
 * Playlist와 Content 간의 다대다(Many-to-Many) 관계를 나타내는 엔티티
 * - 각 플레이리스트에 여러 콘텐츠가 포함될 수 있고,
 * - 각 콘텐츠가 여러 플레이리스트에 포함될 수 있는 구조를 구현
 */

@Entity
@Table(name = "playlist_contents")
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PlaylistContents {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist; // Playlist 엔티티와의 연관관계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Contents content; // Content 엔티티와의 연관관계
}