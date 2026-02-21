package com.creatival.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.creatival.content.Episode;
import java.util.List;
import com.creatival.content.Series;


public interface EpisodeRepository extends JpaRepository<Episode, Long> {
	List<Episode> findBySeriesOrderByEpisodeNumAsc(Series series); //순서대로 정렬, 이게 왜 됨?
	List<Episode> findBySeriesAndEpisodeNum(Series series, Integer episodeNum); // 특정 시리즈의 특정 화를 가지고 오는 것
	
}
