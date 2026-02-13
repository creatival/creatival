package com.creatival.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.creatival.content.Content;
import com.creatival.content.Series;
import java.util.List;


public interface SeriesRepository extends JpaRepository<Series, Long> {
	List<Series> findByContent(Content content); //특정 
	List<Series> findByIsEnd(boolean isEnd); // 완결 혹은 연재중인 작품만 가져오기
}
