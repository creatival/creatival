package com.creatival.team;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.creatival.team.Enum.ApplicationStatus;
import com.creatival.user.Users;
import com.creatival.team.Team;


public interface TeamApplicationRepository extends JpaRepository<TeamApplication, Long> {
	boolean existsByTeamAndUserAndStatus(Team team, Users user, ApplicationStatus status);
	List<TeamApplication> findByTeamIdAndStatus(Long teamId, ApplicationStatus status);
}
