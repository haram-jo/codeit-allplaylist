package com.sprint.api.entity.playlists;

import com.sprint.api.common.BaseEntity;
import com.sprint.api.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "playlists")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Playlist extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(name = "subscriber_count", nullable = false)
    @Builder.Default
    private long subscriberCount = 0; // 구독자수 초기값 0으로 세팅

    //플레이리스트에 담긴 컨텐츠와의 연결
    @Builder.Default
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaylistContents> playlistContents = new ArrayList<>();

    //플레이리스트를 구독 중인 정보들과의 연결
    @Builder.Default
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaylistSubscriptions> subscriptions = new ArrayList<>();

    // 플레이리스트 정보 업데이트 메서드
    public void update(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
