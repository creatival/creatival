package com.creatival.team.dto;

import java.time.LocalDateTime;

import com.creatival.team.TeamMember;
import com.creatival.team.Enum.TeamRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class ResponseTeamMember {
	private Long id;
	
	private TeamRole role;
	
	private String position;
	
	private LocalDateTime joinedAt;
	
	private Long userId;
	private String username;
	private String displayname;
	private String profileImgUrl;
	
	public static ResponseTeamMember from(TeamMember teamMember) {
		return ResponseTeamMember.builder()
				.id(teamMember.getId())
				.role(teamMember.getRole())
				.position(teamMember.getPosition())
				.joinedAt(teamMember.getJoinedAt())
				.userId(teamMember.getUser().getId())
				.username(teamMember.getUser().getUsername())
				.displayname(teamMember.getUser().getDisplayName())
				.profileImgUrl(teamMember.getUser().getProfileImgUrl())
				.build();
	}
}
