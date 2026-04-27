package com.creatival.content.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.creatival.content.Content;

import java.util.List;
import java.util.Optional;

import com.creatival.user.Users;
import com.creatival.content.Enum.ContentType;
import com.creatival.content.Enum.Visibility;
import com.creatival.team.Project;




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
	
	List<Content> findByProject(Project project);
	List<Content> findByProjectAndVisibility(Project project, Visibility visibility);
	
	Page<Content> findByType(ContentType type, Pageable pageable);
	
	Page<Content> findByTypeAndVisibility(ContentType type,Pageable pageable, Visibility visibility);
	
	List<Content> findByUserAndTypeOrderByCreatedAtDesc(Users user, ContentType contentType);
	List<Content> findByUserAndTypeAndVisibilityOrderByCreatedAtDesc(Users user,ContentType contentType, Visibility visibility );
	List<Content> findTop4ByUserAndTypeOrderByCreatedAtDesc(Users user, ContentType type);

	List<Content> findByVisibility(Visibility visibility);
	
	@Query("""
		    select distinct c
		    from Content c
		    where lower(c.title) like lower(concat('%', :keyword, '%'))
		       or lower(c.description) like lower(concat('%', :keyword, '%'))
		    order by c.id desc
		""")
		List<Content> searchContents(@Param("keyword") String keyword);
	
}
