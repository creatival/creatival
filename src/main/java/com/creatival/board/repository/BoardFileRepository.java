package com.creatival.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.creatival.board.Board;
import com.creatival.board.BoardFile;
import java.util.List;



public interface BoardFileRepository extends JpaRepository<BoardFile, Long> {
	List<BoardFile> findByBoard(Board board);
	
	void deleteById(Long id);
}
