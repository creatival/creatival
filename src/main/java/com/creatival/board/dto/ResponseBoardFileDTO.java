package com.creatival.board.dto;

import java.time.LocalDateTime;

import com.creatival.board.BoardFile;
import com.creatival.board.Enum.BoardType;
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
public class ResponseBoardFileDTO {
	private Long id;
	private String fileName;
	private String fileUrl;
	private String fileType;
	private String originName;
	
	public static ResponseBoardFileDTO from(BoardFile boardFile) {
		return ResponseBoardFileDTO.builder()
				.id(boardFile.getId())
				.fileName(boardFile.getFileName())
				.fileUrl(boardFile.getFileUrl())
				.fileType(boardFile.getFileType())
				.originName(boardFile.getOriginalFileName())
				.build();
	}
}
