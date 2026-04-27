package com.creatival.board;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.creatival.board.Enum.BoardType;
import com.creatival.board.dto.CreateBoardDTO;
import com.creatival.board.dto.ResponseBoardDetailDTO;
import com.creatival.board.dto.ResponseBoardListDTO;
import com.creatival.board.dto.UpdateBoardDTO;
import com.creatival.board.repository.BoardFileRepository;
import com.creatival.board.repository.BoardRepository;
import com.creatival.content.Content;
import com.creatival.content.ContentFile;
import com.creatival.content.ContentFileService;
import com.creatival.content.ContentService;
import com.creatival.content.Enum.ContentType;
import com.creatival.team.Project;
import com.creatival.team.Team;
import com.creatival.team.TeamService;
import com.creatival.user.Users;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BoardService {


    private final BoardRepository boardRepository;
	private final ContentService contentService;
	private final ContentFileService contentFileService;
	private final TeamService teamService;
	private final BoardFileService boardFileService;

	public Board getBoardById(Long boardId) {
		Optional<Board> board = boardRepository.findById(boardId);
		if(board.isPresent()) {
			return board.get();
		}
		return null;
	}
	public void createBoard(CreateBoardDTO createBoardDTO, Users user) {
		Board board = Board.builder()
				.title(createBoardDTO.getTitle())
				.boardText(createBoardDTO.getBoardText())
				.boardType(createBoardDTO.getBoardType())
				.user(user)
				.build();
		
		if(createBoardDTO.getContentId() != null) {
			Content content = contentService.getContent(createBoardDTO.getContentId());
			if(content==null) {
				new IllegalArgumentException("해당 ID에 content가 존재하지 않습니다.");
			}
			board.setContent(content);
		}
		if(createBoardDTO.getTeamId() != null) {
			Team team = teamService.getTeamById(createBoardDTO.getTeamId());
			if(team==null) {
				new IllegalArgumentException("해당 ID에 team이 존재하지 않습니다.");
			}
			board.setTeam(team);
		}
		if(createBoardDTO.getProjectId() != null) {
			Project project = teamService.getProjectById(createBoardDTO.getProjectId());
			if(project==null) {
				new IllegalArgumentException("해당 ID에 project가 존재하지 않습니다.");
			}
			board.setProject(project);
		}
		
		boardRepository.save(board);
		if(createBoardDTO.getImages() != null) {
			boardFileService.createBoardFile(board, createBoardDTO.getImages(), "Board");
		}
	}
	
	public Page<ResponseBoardListDTO> getBoardList(BoardType boardType, int page) {
		Pageable pageable = PageRequest.of(page, 12, Sort.by("createdAt").descending());
		Page<Board> list = boardRepository.findByBoardType(boardType, pageable);
		return list.map(board -> ResponseBoardListDTO.from(board));
	}

	public ResponseBoardDetailDTO getBoardDetailById(Long boardId) {
		Optional<Board> optional = boardRepository.findById(boardId);
		if(optional.isPresent()) {
			Board board = optional.get();
			if(board.getContent()!=null) { // 콘텐츠가 있는가
				if(board.getContent().getType()==ContentType.ART) {
					ContentFile contentFile = contentFileService.getContentFileThumbnail(board.getContent());
					return ResponseBoardDetailDTO.fromArt(board, contentFile.getFileUrl());
				} else {
					return ResponseBoardDetailDTO.fromNovel(board);
				}
			} else { // 없을 경우
				return ResponseBoardDetailDTO.from(board);
			}
		}
		return null;
		
	}
	@Transactional
	public void viewCountUpById(Long boardId) {
		boardRepository.incrementViewCount(boardId);
		
	}
	public void updateBoard(@Valid UpdateBoardDTO dto, Long boardId) {
		Board board = getBoardById(boardId);
		board.setTitle(dto.getTitle());
		board.setBoardText(dto.getBoardText());
		
		if(dto.getTeamId()!=null) {
			if(board.getTeam()==null || !board.getTeam().getId().equals(dto.getTeamId())) {
				Team team = teamService.getTeamById(dto.getTeamId());
				board.setTeam(team);
			}
		} else {
			board.setTeam(null);
		}
		if(dto.getProjectId()!=null ) {
			if(board.getProject()==null || !board.getProject().getId().equals(dto.getProjectId())) {
				Project project = teamService.getProjectById(dto.getProjectId());
				board.setProject(project);
			}
		} else {
			board.setProject(null);
		}
		
		if(dto.getContentId()!=null) {
			if(board.getContent()==null || !board.getContent().getId().equals(dto.getContentId())) {
				Content content = contentService.getContent(dto.getContentId());
				board.setContent(content);
			}
		} else {
			board.setContent(null);
		}
		if(dto.getDeleteFileIds()!=null) {
			for(Long id : dto.getDeleteFileIds()) {
				boardFileService.deleteById(id);
			}
		}
		
		if(dto.getImages()!=null) {
			boardFileService.createBoardFile(board, dto.getImages(), "Board");
		}
	}
	public void deleteBoard(Board board) {
		boardRepository.delete(board);
		
	}

	
}
