package com.creatival.comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.creatival.FileUtil;
import com.creatival.board.Board;
import com.creatival.board.BoardService;
import com.creatival.board.repository.BoardFileRepository;
import com.creatival.comment.dto.ResponseCommentDTO;
import com.creatival.comment.repository.CommentRepository;
import com.creatival.content.Content;
import com.creatival.content.ContentService;
import com.creatival.team.Team;
import com.creatival.team.TeamService;
import com.creatival.user.Users;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentService {
	private final CommentRepository commentRepository;
	private final ContentService contentService;
	private final BoardService boardService;
	private final TeamService teamService;
	
	public int getCountByContent(Long id) {
		Content content = contentService.getContent(id);
		if(content == null) {
			throw new IllegalArgumentException("댓글을 입력하려는 content를 찾을 수 없습니다.");
		}
		
		return commentRepository.countByContent(content);
	}
	
	public List<ResponseCommentDTO> getCommentListByContent(Long id){
		Content content = contentService.getContent(id);
		
		if(content == null) {
			throw new IllegalArgumentException("댓글을 입력하려는 content를 찾을 수 없습니다.");
		}
		List<ResponseCommentDTO> rootComments = new ArrayList<>();
		Map<Long, ResponseCommentDTO> map = new HashMap<>();
		List<Comment> list = commentRepository.findByContent(content);
		if (list == null || list.isEmpty()) return new ArrayList<>();
		
		for(Comment comment : list) {
			if (comment == null) continue;
			ResponseCommentDTO dto = ResponseCommentDTO.from(comment);
			map.put(dto.getId(), dto);
		}
		
		// 간단하게 부모 comment가 존재한다면 가져와서 거기에 자식으로 연결시키는 방식임, map은 index로 활용하기 위함임
		for(Comment comment : list) {
			ResponseCommentDTO dto = map.get(comment.getId());
			if (dto == null) continue;
			if(comment.getParentComment() != null) {
				ResponseCommentDTO parentDTO = map.get(comment.getParentComment().getId());
				if(parentDTO != null) {
					parentDTO.getChildren().add(dto);
				}
			} else {
				rootComments.add(dto);
			}
		}
		rootComments.removeIf(Objects::isNull);
		return rootComments;
	}
	public List<ResponseCommentDTO> getCommentListByBoard(Long id){
		Board board = boardService.getBoardById(id);
		
		if(board == null) {
			throw new IllegalArgumentException("댓글을 입력하려는 board를 찾을 수 없습니다.");
		}
		List<ResponseCommentDTO> rootComments = new ArrayList<>();
		Map<Long, ResponseCommentDTO> map = new HashMap<>();
		List<Comment> list = commentRepository.findByBoard(board);
		if (list == null || list.isEmpty()) return new ArrayList<>();
		
		for(Comment comment : list) {
			if (comment == null) continue;
			ResponseCommentDTO dto = ResponseCommentDTO.from(comment);
			map.put(dto.getId(), dto);
		}
		
		// 간단하게 부모 comment가 존재한다면 가져와서 거기에 자식으로 연결시키는 방식임, map은 index로 활용하기 위함임
		for(Comment comment : list) {
			ResponseCommentDTO dto = map.get(comment.getId());
			if (dto == null) continue;
			if(comment.getParentComment() != null) {
				ResponseCommentDTO parentDTO = map.get(comment.getParentComment().getId());
				if(parentDTO != null) {
					parentDTO.getChildren().add(dto);
				}
			} else {
				rootComments.add(dto);
			}
		}
		rootComments.removeIf(Objects::isNull);
		return rootComments;
	}
	
	public Comment getCommentById(Long id) {
		Optional<Comment> optional = commentRepository.findById(id);
		if(optional.isEmpty()) {
			return null;
		}
		return optional.get();
	}
	
	public void createCommentForContent(Long id, String text, Users user) {
		Content content = contentService.getContent(id);
		if(content == null) {
			throw new IllegalArgumentException("댓글을 입력하려는 content를 찾을 수 없습니다.");
		}
		Comment comment = Comment.builder()
			.text(text)
			.user(user)
			.content(content)
			.build();
		
		commentRepository.save(comment);
	}

	public void updateCommentForContent(Long id, String text) {
		Comment comment = commentRepository.findById(id).get();
		comment.setText(text);
		commentRepository.save(comment);
	}

	public void delete(Comment comment) {
		commentRepository.delete(comment);
		
	}

	public void createCommentForComment(Long id, String text, Users user) {
		Comment comment = getCommentById(id);
		if(comment == null) {
			throw new IllegalArgumentException("댓글을 입력하려는 comment를 찾을 수 없습니다.");
		}
		Comment replyComment = Comment.builder()
			.text(text)
			.user(user)
			.parentComment(comment)
			.build();
		
		if(comment.getContent()!=null) {
			replyComment.setContent(comment.getContent());
		} else if(comment.getBoard()!=null) {
			replyComment.setBoard(comment.getBoard());
		}
		
		commentRepository.save(replyComment);
	}

	public void createCommentForBoard(Long id, String text, Users user) {
		Board board = boardService.getBoardById(id);
		if(board == null) {
			throw new IllegalArgumentException("댓글을 입력하려는 content를 찾을 수 없습니다.");
		}
		Comment comment = Comment.builder()
			.text(text)
			.user(user)
			.board(board)
			.build();
		
		commentRepository.save(comment);
	}

	public int getCountByBoard(Long boardId) {
		Board board = boardService.getBoardById(boardId);
		if(board == null) {
			throw new IllegalArgumentException("댓글을 입력하려는 board를 찾을 수 없습니다.");
		}
		
		return commentRepository.countByBoard(board);
	}

	public void createCommentForTeam(Long id, String text, Users user) {
		Team team = teamService.getTeamById(id);
		if(team == null) {
			throw new IllegalArgumentException("댓글을 입력하려는 content를 찾을 수 없습니다.");
		}
		Comment comment = Comment.builder()
				.text(text)
				.user(user)
				.team(team)
				.build();
		commentRepository.save(comment);
		
	}

	public List<ResponseCommentDTO> getCommentListByTeam(Long id){
		Team team = teamService.getTeamById(id);
		
		if(team == null) {
			throw new IllegalArgumentException("댓글을 입력하려는 content를 찾을 수 없습니다.");
		}
		List<ResponseCommentDTO> rootComments = new ArrayList<>();
		List<Comment> list = commentRepository.findTop5ByTeamOrderByCreatedAt(team);
		if (list == null || list.isEmpty()) return new ArrayList<>();
		
		for(Comment comment : list) {
			if (comment == null) continue;
			ResponseCommentDTO dto = ResponseCommentDTO.from(comment);
			rootComments.add(dto);
		}
		rootComments.removeIf(Objects::isNull);
		return rootComments;
	}

}
