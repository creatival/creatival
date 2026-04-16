package com.creatival.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.creatival.content.Content;
import com.creatival.content.ContentFile;
import java.util.List;
import com.creatival.content.Episode;


public interface ContentFileRepository extends JpaRepository<ContentFile, Long	> {
	List<ContentFile> findByEpisodeOrderBySortOrder(Episode episode);
	List<ContentFile> findByFileType(String fileType); // 파일 타입별로 가져오기
	ContentFile findTopByContentOrderBySortOrderAsc(Content content);
	List<ContentFile> findByContent(Content content);
	ContentFile findTop1ByEpisodeOrderBySortOrder(Episode episode);
	@Query("SELECT COALESCE(MAX(cf.sortOrder), 0) FROM ContentFile cf WHERE cf.episode = :episode")
	int findMaxSortOrderByEpisode(@Param("episode") Episode episode);
	
}
