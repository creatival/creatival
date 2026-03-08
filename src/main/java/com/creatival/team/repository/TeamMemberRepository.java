package com.creatival.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.creatival.user.Users;
import com.creatival.team.Team;
import com.creatival.team.TeamMember;



public interface TeamMemberRepository extends JpaRepository<TeamMember, Long>{
	List<TeamMember> findByTeam(Team team);
	boolean existsByTeamAndUser(Team team, Users user);
}
