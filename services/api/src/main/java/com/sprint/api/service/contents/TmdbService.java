package com.sprint.api.service.contents;

import com.sprint.api.dto.contents.TmdbMovieResponse;
import com.sprint.api.entity.contents.ContentTag;
import com.sprint.api.entity.contents.Contents;
import com.sprint.api.entity.contents.Tag;
import com.sprint.api.repository.contents.ContentsRepository;
import com.sprint.api.repository.contents.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;


/**
 * TMDB에서 인기 영화 데이터를 가져와서 Contents로 등록하는 서비스
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class TmdbService {
    private final RestClient tmdbRestClient;
    private final ContentsRepository contentsRepository;
    private final TagRepository tagRepository;

    // Tmdb 콘텐츠 태그  (예: 28 -> "액션")
    private static final Map<Integer, String> GENRE_MAP = new HashMap<>();
    static {
        GENRE_MAP.put(28, "액션");
        GENRE_MAP.put(12, "모험");
        GENRE_MAP.put(16, "애니메이션");
        GENRE_MAP.put(35, "코미디");
        GENRE_MAP.put(80, "범죄");
        GENRE_MAP.put(99, "다큐멘터리");
        GENRE_MAP.put(18, "드라마");
        GENRE_MAP.put(10751, "가족");
        GENRE_MAP.put(14, "판타지");
        GENRE_MAP.put(36, "역사");
        GENRE_MAP.put(27, "공포");
        GENRE_MAP.put(10402, "음악");
        GENRE_MAP.put(9648, "미스터리");
        GENRE_MAP.put(10749, "로맨스");
        GENRE_MAP.put(878, "SF");
        GENRE_MAP.put(53, "스릴러");
    }

    @Transactional
    public void createContentsFromTmdb() {
        // 1. 외부 응답 DTO(Response)로 데이터 수신
        TmdbMovieResponse response = tmdbRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/popular")
                        .queryParam("language", "ko-KR")
                        .build())
                .retrieve()
                .body(TmdbMovieResponse.class);

        if (response == null || response.results() == null) return;

        // 2. 규칙에 따라 Repository의 save()를 호출하여 등록
        for (var tmdbMovie : response.results()) {

            // 중복 방지를 위해 tmdbId 필드 체크가 필요
            if (contentsRepository.existsByTmdbId(tmdbMovie.id())) {
                continue;
            }

            // 저장할 데이터 조립 (Entity)
            Contents content = Contents.builder()
                    .tmdbId(tmdbMovie.id()) // Contents 엔티티에 추가 권장
                    .type("MOVIE")
                    .title(tmdbMovie.title())
                    .description(tmdbMovie.description())
                    .thumbnailUrl("https://image.tmdb.org/t/p/w500" + tmdbMovie.posterPath())
                    .averageRating(tmdbMovie.voteAverage().intValue())
                    .reviewCount(0)
                    .watcherCount(0L)
                    .build();

            // 장르 ID 리스트를 순회하며 태그 연결
            if (tmdbMovie.genreIds() != null) {
                for (Integer genreId : tmdbMovie.genreIds()) {
                    String tagName = GENRE_MAP.get(genreId);

                    if (tagName != null) {
                        // Tag 테이블에서 찾고, 없으면 새로 생성
                        Tag tag = tagRepository.findByTag(tagName)
                                .orElseGet(() -> tagRepository.save(new Tag(tagName)));

                        // Contents와 Tag를 연결하는 ContentTag 엔티티 생성
                        ContentTag contentTag = ContentTag.builder()
                                .contents(content)
                                .tag(tag)
                                .build();

                        // 엔티티에 정의된 연관 관계 편의 메서드 사용
                        content.addTag(contentTag);
                    }
                }
            }

            //  실제 DB 저장 (INSERT 실행)
            contentsRepository.save(content);
        }
        log.info("TMDB 인기 영화 등록 완료");
    }
}
