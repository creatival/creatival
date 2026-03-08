package com.creatival.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.creatival.team.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

}
