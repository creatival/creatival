package com.creatival.team.dto;

import java.time.LocalDateTime;

import com.creatival.content.Enum.Visibility;
import com.creatival.team.Project;
import com.creatival.team.Team;
import com.creatival.team.Enum.TeamStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class ResponseProjectDeatilDTO {
	private Long id;
	private String title;
	private String description;
	private TeamStatus status;
	private int totalProgress;
	private String bannerImgUrl;
	private LocalDateTime createdAt;
	private LocalDateTime endDate;
	private Visibility visibility;
	
	//team 관련
	private Long teamId;
	private String teamName;
	private String profileImgUrl;
	private LocalDateTime teamCreatedAt;
	
	public static ResponseProjectDeatilDTO from(Project project) {
		return ResponseProjectDeatilDTO.builder()
				.id(project.getId())
				.title(project.getTitle())
				.description(project.getDescription())
				.status(project.getStatus())
				.totalProgress(project.getTotalProgress())
				.bannerImgUrl(project.getBannerImgUrl())
				.createdAt(project.getCreatedAt())
				.endDate(project.getEndDate())
				.visibility(project.getVisibility())
				.teamId(project.getTeam().getId())
				.teamName(project.getTeam().getName())
				.profileImgUrl(project.getTeam().getProfileImgUrl())
				.teamCreatedAt(project.getTeam().getCreatedAt())
				.build();
	}
}
