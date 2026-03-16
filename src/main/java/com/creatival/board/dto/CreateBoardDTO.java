package com.creatival.board.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.creatival.board.Enum.BoardType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@Builder
@AllArgsConstructor
public class CreateBoardDTO {
	@NotEmpty(message =  "제목은 필수 사항입니다.")
	private String title;

	private String boardText;
	
	@NotNull(message =  "필수적으로 체크해야합니다.")
	private BoardType boardType;
	
	private Long contentId;
	private Long projectId;
	private Long teamId;
	
	private List<MultipartFile> images;
}
