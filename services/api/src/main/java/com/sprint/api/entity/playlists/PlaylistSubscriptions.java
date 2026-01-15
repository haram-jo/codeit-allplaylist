package com.sprint.api.entity.playlists;

import com.sprint.api.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

/**
 * Playlist와 User 간의 다대다(Many-to-Many) 관계를 나타내는 엔티티
 * - 각 사용자가 여러 플레이리스트를 구독할 수 있고,
 * - 각 플레이리스트가 여러 사용자에게 구독될 수 있는 구조를 구현
 */


@Entity
@Table(name = "playlist_subscriptions")
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PlaylistSubscriptions {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // User 엔티티와의 연관관계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist; // Playlist 엔티티와의 연관관계
}