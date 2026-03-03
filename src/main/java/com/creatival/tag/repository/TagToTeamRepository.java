package com.creatival.tag.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.creatival.tag.TagToTeam;
import java.util.List;
import com.creatival.team.Team;


public interface TagToTeamRepository extends JpaRepository<TagToTeam, Long> {
	List<TagToTeam> findByTeam(Team team);
}
