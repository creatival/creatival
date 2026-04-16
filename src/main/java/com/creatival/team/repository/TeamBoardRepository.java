package com.creatival.team.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.creatival.team.Project;
import com.creatival.team.Team;
import com.creatival.team.TeamBoard;

public interface TeamBoardRepository extends JpaRepository<TeamBoard, Long> {

	List<TeamBoard> findByTeamOrderByCreatedAtDesc(Team team);
	Optional<TeamBoard> findTop1ByTeamAndNoticeOrderByCreatedAtDesc(Team team, boolean notice);
	List<TeamBoard> findByProjectOrderByCreatedAtDesc(Project project);
	Optional<TeamBoard> findTop1ByProjectAndNoticeOrderByCreatedAtDesc(Project project, boolean notice);
}
