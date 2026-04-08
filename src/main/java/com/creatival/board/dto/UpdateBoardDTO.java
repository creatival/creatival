package com.creatival.board.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.creatival.board.Board;
import com.creatival.board.Enum.BoardType;
import com.creatival.content.DTO.ResponseContentListForProject;
import com.creatival.team.dto.ResponseProjectListDTO;
import com.creatival.team.dto.ResponseTeamListDTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class UpdateBoardDTO {
	@NotEmpty(message =  "제목은 필수 사항입니다.")
	private String title;

	private String boardText;
	
	private BoardType boardType;
	
	
	private Long contentId;
	private Long projectId;
	private Long teamId;
	
	private List<MultipartFile> images;
	
	private List<Long> deleteFileIds;
	
	private List<ResponseBoardFileDTO> files;
	
	public static UpdateBoardDTO from(Board board, List<ResponseBoardFileDTO> files) {
		return UpdateBoardDTO.builder()
				.title(board.getTitle())
				.boardText(board.getBoardText())
				.boardType(board.getBoardType())
				.contentId(board.getContent()!=null ? board.getContent().getId() : null)
				.projectId(board.getProject() !=null ? board.getProject().getId() : null)
				.teamId(board.getTeam() != null ? board.getTeam().getId() : null)
				.files(files)
				.build();
	}
}
