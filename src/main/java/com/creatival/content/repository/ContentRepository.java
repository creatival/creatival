package com.creatival.content.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.creatival.content.Content;

import java.util.List;
import java.util.Optional;

import com.creatival.user.Users;
import com.creatival.content.Enum.ContentType;



public interface ContentRepository extends JpaRepository<Content, Long> {
	Optional<Content> findById(Long id);
	
	List<Content> findByUser(Users user);
//	List<Content> findByTeam(Team team);
//	List<Content> findByProject(Project project);
	
	//타입별(소설, 만화, 그림등등)
	List<Content> findByType(String type);
	
	//특정 2차 창작인거 가져오기
	List<Content> findByOriginalContent(Content originalContent);
	
	List<Content> findByTitleContaining(String title); //제목 키워드로 가져오기
	
	Page<Content> findByType(ContentType type, Pageable pageable);
}
