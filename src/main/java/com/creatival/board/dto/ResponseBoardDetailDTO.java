package com.creatival.board.dto;

import java.time.LocalDateTime;

import com.creatival.board.Board;
import com.creatival.board.Enum.BoardType;
import com.creatival.content.ContentFile;
import com.creatival.content.DTO.ResponseContentListForProject;
import com.creatival.team.dto.ResponseProjectListDTO;
import com.creatival.team.dto.ResponseTeamListDTO;

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
public class ResponseBoardDetailDTO {
	private Long id;

	private String title;

	private String boardText;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private BoardType boardType;

	private Long viewCount;

	private String username;
	private String displayname;

	private ResponseTeamListDTO team;
	private ResponseContentListForProject content;
	private ResponseProjectListDTO project;

	public static ResponseBoardDetailDTO from(Board board) {

		ResponseTeamListDTO teamDTO = null;
		ResponseProjectListDTO projectDTO = null;

		if (board.getTeam() != null) {
			teamDTO = ResponseTeamListDTO.from(board.getTeam());
		}

		if (board.getProject() != null) {
			projectDTO = ResponseProjectListDTO.from(board.getProject());
		}
		return ResponseBoardDetailDTO.builder().id(board.getId()).title(board.getTitle())
				.boardText(board.getBoardText()).createdAt(board.getCreatedAt()).updatedAt(board.getUpdatedAt())
				.boardType(board.getBoardType()).viewCount(board.getViewCount()).username(board.getUser().getUsername())
				.displayname(board.getUser().getDisplayName()).team(teamDTO).project(projectDTO).build();
	}

	public static ResponseBoardDetailDTO fromNovel(Board board) {

		ResponseBoardDetailDTO dto = from(board);

		dto.setContent(ResponseContentListForProject.fromNovel(board.getContent()));

		return dto;
	}

	public static ResponseBoardDetailDTO fromArt(Board board, String thumbnailUrl) {

		ResponseBoardDetailDTO dto = from(board);

		dto.setContent(ResponseContentListForProject.fromArt(board.getContent(), thumbnailUrl));

		return dto;
	}

}
