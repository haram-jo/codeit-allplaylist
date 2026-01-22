package com.sprint.api.kafka;

import java.util.UUID;

/**
 * 이벤트 발생 시점: 사용자가 플레이리스트를 구독했을 때 정보담는 DTO
 *
 * @param playlistId   API 요청에서 받아옴, 어떤 플레이리스트를 구독했는지
 * @param subscriberId 인증된 사용자 정보에서 추출, @AuthenticationPrincipal
 * @param ownerId      DB에서 조회, 해당 플리아이디에 해당하는 주인의 ID
 */


public record PlaylistSubscribedEvent(
        UUID playlistId, // 플리 아이디
        UUID subscriberId, // 구독자 아이디
        String subscriberName, // 구독자 이름
        UUID ownerId // 플리 주인 아이디
        ) {}
