package com.creatival.team.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.creatival.team.History;
import com.creatival.team.Project;

import java.util.List;


public interface HistoryRepository extends JpaRepository<History, Long> {
	List<History> findByProjectOrderByCreatedAtDesc(Project project);
	Optional<History> findTop1ByProjectOrderByCreatedAtDesc(Project project);
}
