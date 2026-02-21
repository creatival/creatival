package com.creatival.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.creatival.content.ContentFile;
import java.util.List;
import com.creatival.content.Episode;


public interface ContentFileRepository extends JpaRepository<ContentFile, Long	> {
	List<ContentFile> findByEpisodeOrderBySortOrder(Episode episode);
	List<ContentFile> findByFileType(String fileType); // 파일 타입별로 가져오기
}
