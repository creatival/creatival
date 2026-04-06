package com.creatival.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.creatival.board.Board;
import com.creatival.comment.Comment;
import java.util.List;
import com.creatival.content.Content;


public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByContent(Content content);
	int countByContent(Content content);
	List<Comment> findByBoard(Board board);
	int countByBoard(Board board);
}
