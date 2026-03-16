package com.creatival.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.creatival.board.BoardFile;


public interface BoardFileRepository extends JpaRepository<BoardFile, Long> {
	
}
