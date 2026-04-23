package com.creatival.team.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.creatival.content.Enum.Visibility;
import com.creatival.team.Team;

import java.util.List;


public interface TeamRepository extends JpaRepository<Team, Long> {
	Page<Team> findByVisibility(Visibility visibility, Pageable pageable);
	
	@Query("""
		    select distinct t
		    from Team t
		    where lower(t.name) like lower(concat('%', :keyword, '%'))
		       or lower(t.description) like lower(concat('%', :keyword, '%'))
		    order by t.id desc
		""")
		List<Team> searchTeams(@Param("keyword") String keyword);
}
