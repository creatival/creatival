package com.creatival.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.creatival.board.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {

}
