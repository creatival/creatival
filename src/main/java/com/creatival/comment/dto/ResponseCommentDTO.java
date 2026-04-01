package com.creatival.comment.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.creatival.board.Enum.BoardType;
import com.creatival.board.dto.ResponseBoardDetailDTO;
import com.creatival.comment.Comment;
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
public class ResponseCommentDTO {
	private Long id;
	private String text;
	
	private String username;
	private String displayname;
	
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	private Long likeCount;
	
	@Builder.Default
	private List<ResponseCommentDTO> children = new ArrayList<>();
	
	public static ResponseCommentDTO from(Comment comment) {
		return ResponseCommentDTO.builder()
				.id(comment.getId())
				.text(comment.getText())
				.username(comment.getUser().getUsername())
				.displayname(comment.getUser().getDisplayName())
				.createdAt(comment.getCreatedAt())
				.updatedAt(comment.getUpdatedAt())
				.likeCount(comment.getLikeCount())
				.children(new ArrayList<>())
				.build();
	}
	
}
