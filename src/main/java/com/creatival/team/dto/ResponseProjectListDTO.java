package com.creatival.team.dto;

import java.time.LocalDateTime;

import com.creatival.team.Project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class ResponseProjectListDTO {
	private Long id;
	
	private String title;
	
	private String bannerImgUrl;
	
	private LocalDateTime createdAt;
	
	private String teamName;
	
	public static ResponseProjectListDTO from(Project project) {
		return ResponseProjectListDTO.builder()
				.id(project.getId())
				.title(project.getTitle())
				.bannerImgUrl(project.getBannerImgUrl())
				.createdAt(project.getCreatedAt())
				.teamName(project.getTeam().getName())
				.build(); 
	}
}
