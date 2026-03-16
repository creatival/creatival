package com.creatival.board.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.creatival.board.Board;
import com.creatival.board.Enum.BoardType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@Builder
@AllArgsConstructor
public class ResponseBoardListDTO {
	private Long id;
	
	private String title;
	
	private String username;
	
	private String displayname;
	
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	private Long viewCount;
	
	
	public static ResponseBoardListDTO from(Board board) {
		return ResponseBoardListDTO.builder()
				.id(board.getId())
				.title(board.getTitle())
				.username(board.getUser().getUsername())
				.displayname(board.getUser().getDisplayName())
				.createdAt(board.getCreatedAt())
				.updatedAt(board.getUpdatedAt())
				.viewCount(board.getViewCount())
				.build();
	}
}
