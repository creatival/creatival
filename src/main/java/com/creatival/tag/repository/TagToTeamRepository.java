package com.creatival.tag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.creatival.tag.TagToTeam;
import java.util.List;
import com.creatival.team.Team;

import jakarta.transaction.Transactional;


public interface TagToTeamRepository extends JpaRepository<TagToTeam, Long> {
	List<TagToTeam> findByTeam(Team team);
	
	@Modifying
    @Transactional
    @Query("DELETE FROM TagToTeam tm WHERE tm.team.id = :teamId AND tm.tag.id = :tagId")
    void deleteByTeamIdAndTagId(@Param("teamId") Long teamId, @Param("tagId") Long tagId);
}
