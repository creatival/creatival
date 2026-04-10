package com.creatival.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Streamable;

import com.creatival.board.Board;
import com.creatival.comment.Comment;
import com.creatival.comment.dto.ResponseCommentDTO;

import java.util.List;
import com.creatival.content.Content;
import com.creatival.content.Episode;
import com.creatival.team.Team;


public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByContent(Content content);
	int countByContent(Content content);
	List<Comment> findByBoard(Board board);
	int countByBoard(Board board);
	List<Comment> findByTeam(Team team);
	List<Comment> findTop5ByTeamOrderByCreatedAt(Team team);
	Page<Comment> findByTeam(Team team, Pageable pageable);
	List<Comment> findByEpisode(Episode episode);
	int countByEpisode(Episode episode);
}
