package com.creatival.team.dto;

import org.springframework.web.multipart.MultipartFile;

import com.creatival.team.TeamBoard;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class UpdateTeamBoardDTO {
	
	private Long id;
	
	@NotEmpty
	@Size(max = 100)
	private String title;
	
	@NotEmpty
	private String text;
	
	private MultipartFile img;
	
	private String imgUrl; // 기존 이미지
	
	private boolean notice;
	
	public static UpdateTeamBoardDTO from(TeamBoard teamBoard) {
		UpdateTeamBoardDTO dto = UpdateTeamBoardDTO.builder()
				.id(teamBoard.getId())
				.title(teamBoard.getTitle())
				.text(teamBoard.getText())
				.imgUrl(teamBoard.getImgUrl())
				.notice(teamBoard.isNotice())
				.build();
		if(teamBoard.getImgUrl()!=null && !teamBoard.getImgUrl().isEmpty()) {
			dto.setImgUrl(teamBoard.getImgUrl());
		}
		return dto;
		
	}
}
