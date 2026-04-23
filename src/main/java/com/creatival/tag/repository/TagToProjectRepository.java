package com.creatival.tag.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.creatival.tag.TagToProject;
import com.creatival.team.Project;

public interface TagToProjectRepository extends JpaRepository<TagToProject, Long> {
	@Query("""
		    select distinct project
		    from TagToProject ttp
		    join ttp.tag t
		    join ttp.project project
		    where lower(t.tagText) = lower(:keyword)
		    order by project.id desc
		""")
		List<Project> searchProjectsByTag(@Param("keyword") String keyword);
}
