package com.creatival.team.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@AllArgsConstructor
public class CreateTeamBoardDTO {
	@NotEmpty
	@Size(max = 100)
	private String title;
	
	@NotEmpty
	private String text;
	
	private MultipartFile img;
	
	private boolean notice;
}
