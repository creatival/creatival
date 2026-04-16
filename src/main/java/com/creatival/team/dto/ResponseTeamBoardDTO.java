package com.creatival.team.dto;

import java.time.LocalDateTime;

import com.creatival.team.Project;
import com.creatival.team.Team;
import com.creatival.team.TeamBoard;
import com.creatival.team.Enum.ApplicationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class ResponseTeamBoardDTO {
	private Long id;
	private String title;
	private String text;
	private String imgUrl;
	private boolean notice;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	//user
	private Long userId;
	private String username;
	private String displayname;
	private String profileImgUrl;
	
	//team
	private Long teamId;
	private String teamName;
	
	//project
	private Long projectId;
	private String projectName;
	
	public static ResponseTeamBoardDTO fromTeam(TeamBoard teamBoard) {
		ResponseTeamBoardDTO dto =  ResponseTeamBoardDTO.builder()
				.id(teamBoard.getId())
				.title(teamBoard.getTitle())
				.text(teamBoard.getText())
				.notice(teamBoard.isNotice())
				.createdAt(teamBoard.getCreatedAt())
				.updatedAt(teamBoard.getUpdatedAt())
				.userId(teamBoard.getUser().getId())
				.username(teamBoard.getUser().getUsername())
				.displayname(teamBoard.getUser().getDisplayName())
				.teamId(teamBoard.getTeam().getId())
				.teamName(teamBoard.getTeam().getName())
				.build();
		if(teamBoard.getUser().getProfileImgUrl()!=null) {
			dto.setProfileImgUrl(teamBoard.getUser().getProfileImgUrl());
		}
		if(teamBoard.getImgUrl()!=null) {
			dto.setImgUrl(teamBoard.getImgUrl());
		}
		return dto;
	}
	
	public static ResponseTeamBoardDTO fromProject(TeamBoard teamBoard) {
		ResponseTeamBoardDTO dto =  ResponseTeamBoardDTO.builder()
				.id(teamBoard.getId())
				.title(teamBoard.getTitle())
				.text(teamBoard.getText())
				.notice(teamBoard.isNotice())
				.createdAt(teamBoard.getCreatedAt())
				.updatedAt(teamBoard.getUpdatedAt())
				.userId(teamBoard.getUser().getId())
				.username(teamBoard.getUser().getUsername())
				.displayname(teamBoard.getUser().getDisplayName())
				.projectId(teamBoard.getProject().getId())
				.projectName(teamBoard.getProject().getTitle())
				.build();
		if(teamBoard.getUser().getProfileImgUrl()!=null) {
			dto.setProfileImgUrl(teamBoard.getUser().getProfileImgUrl());
		}
		if(teamBoard.getImgUrl()!=null) {
			dto.setImgUrl(teamBoard.getImgUrl());
		}
		return dto;
	}
}
