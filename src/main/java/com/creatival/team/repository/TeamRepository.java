package com.creatival.team.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.creatival.content.Enum.Visibility;
import com.creatival.team.Team;

import java.util.List;


public interface TeamRepository extends JpaRepository<Team, Long> {
	Page<Team> findByVisibility(Visibility visibility, Pageable pageable);
}
