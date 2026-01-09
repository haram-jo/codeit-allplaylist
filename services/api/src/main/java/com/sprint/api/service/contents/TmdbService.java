package com.sprint.api.service.contents;

import com.sprint.api.dto.contents.TmdbMovieResponse;
import com.sprint.api.entity.contents.Contents;
import com.sprint.api.repository.contents.ContentsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;


/**
 * TMDB에서 인기 영화 데이터를 가져와서 Contents로 등록하는 서비스
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class TmdbService {
    private final RestClient tmdbRestClient;
    private final ContentsRepository contentsRepository;

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

            //  실제 DB 저장 (INSERT 실행)
            contentsRepository.save(content);
        }
        log.info("TMDB 인기 영화 등록 완료");
    }
}
