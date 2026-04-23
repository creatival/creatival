package com.creatival.tag.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.creatival.content.Content;
import com.creatival.tag.TagToContent;

import jakarta.transaction.Transactional;

public interface TagToContentRepository extends JpaRepository<TagToContent, Long> {
	List<TagToContent> findByContent(Content content);
	
	@Modifying
    @Transactional
    @Query("DELETE FROM TagToContent tm WHERE tm.content.id = :contentId AND tm.tag.id = :tagId")
    void deleteByContentIdAndTagId(@Param("contentId") Long contentId, @Param("tagId") Long tagId);
	
	void deleteByContent(Content content);
	
	@Query("""
		    select distinct content
		    from TagToContent ttc
		    join ttc.tag t
		    join ttc.content content
		    where lower(t.tagText) = lower(:keyword)
		    order by content.id desc
		""")
		List<Content> searchContentsByTag(@Param("keyword") String keyword);
}
