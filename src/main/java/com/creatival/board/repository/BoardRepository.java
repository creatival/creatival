package com.creatival.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.creatival.board.Board;
import java.util.List;
import com.creatival.board.Enum.BoardType;


public interface BoardRepository extends JpaRepository<Board, Long> {
	Page<Board> findByBoardType(BoardType boardType, Pageable pageable);
}
