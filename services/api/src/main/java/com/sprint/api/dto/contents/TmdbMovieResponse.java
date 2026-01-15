package com.sprint.api.dto.contents;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * TMDB 서버가 나에게 보내준 응답(Response) 데이터
 * - 아직 우리 DB에 들어가기 전 상태
 */

public record TmdbMovieResponse(
        List<TmdbMovieDto> results
) {
    public record TmdbMovieDto(
            Long id,                // 중복 체크용 (tmdbId)
            String title,
            @JsonProperty("overview")
            String description,     // TMDB의 필드명을 우리 필드명으로 매핑
            @JsonProperty("poster_path")
            String posterPath,
            @JsonProperty("vote_average")
            Double voteAverage,
            @JsonProperty("genre_ids") // TMDB에서 보내주는 장르 ID 리스트
            List<Integer> genreIds
    ) {}
}