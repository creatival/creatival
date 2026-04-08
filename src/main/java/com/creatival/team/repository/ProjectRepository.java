package com.creatival.team.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.creatival.content.Enum.Visibility;
import com.creatival.team.Project;
import com.creatival.team.Team;

public interface ProjectRepository extends JpaRepository<Project, Long> {
	List<Project> findByTeamAndVisibility(Team team, Visibility visibility);
	Page<Project> findByTeamAndVisibility(Team team, Visibility visibility, Pageable pageable);
	Optional<Project> findByProjectTag(String projectTag);
	List<Project> findByTeam(Team team);
}
