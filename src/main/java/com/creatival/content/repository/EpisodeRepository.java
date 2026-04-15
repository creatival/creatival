package com.creatival.content.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.creatival.content.Episode;
import java.util.List;
import java.util.Optional;

import com.creatival.content.Series;
import com.creatival.content.DTO.ResponseNovelList;


public interface EpisodeRepository extends JpaRepository<Episode, Long> {
	Page<Episode> findBySeriesOrderByEpisodeNumAsc(Series series, Pageable pageable); //순서대로 정렬, 이게 왜 됨?
	Optional<Episode> findBySeriesAndEpisodeNum(Series series, Integer episodeNum); // 특정 시리즈의 특정 화를 가지고 오는 것
	
	@Query("SELECT MAX(e.episodeNum) FROM Episode AS e WHERE e.series = :series")
	Integer findByMaxEpisodeNumBySeries(@Param("series") Series series);
	
	int countBySeries(Series series);
	
	//이전 회차, 현재 episodeNum보다 작으면서 가장 큰 값을 가져옴
	@EntityGraph(attributePaths = "series")
	Optional<Episode> findFirstBySeriesAndEpisodeNumLessThanOrderByEpisodeNumDesc(Series series, int episodeNum);
	
	//다음 회차, 현재 episodeNum보다 크면서 가장 작은 값을 가져옴
	@EntityGraph(attributePaths = "series")
	Optional<Episode> findFirstBySeriesAndEpisodeNumGreaterThanOrderByEpisodeNumAsc(Series series, int episodeNum);
	
	Optional<Episode> findFirstBySeriesOrderByEpisodeNumAsc(Series series);
	
}
