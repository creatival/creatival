package com.creatival.team.dto;

import java.time.LocalDateTime;

import com.creatival.content.Enum.Visibility;
import com.creatival.team.History;
import com.creatival.team.Enum.TeamStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class ResponseHistoryDTO {
	private Long id;
	private String versionTag;
	private String title;
	private String text;
	private LocalDateTime createdAt;
	
	private Long teamId;
	private String teamName;
	
	private Long projectId;
	private String projectName;
	
	private Long userId;
	private String username;
	private String displayname;
	
	public static ResponseHistoryDTO from(History history) {
		return ResponseHistoryDTO.builder()
				.id(history.getId())
				.versionTag(history.getVersionTag())
				.title(history.getTitle())
				.text(history.getText())
				.createdAt(history.getCreatedAt())
				.teamId(history.getProject().getTeam().getId())
				.teamName(history.getProject().getTeam().getName())
				.projectId(history.getProject().getId())
				.projectName(history.getProject().getTitle())
				.userId(history.getUser().getId())
				.username(history.getUser().getUsername())
				.displayname(history.getUser().getDisplayName())
				.build();
				
	}
}
