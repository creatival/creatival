package com.creatival.team.dto;

import java.time.LocalDateTime;

import com.creatival.team.TeamApplication;
import com.creatival.team.Enum.ApplicationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class ResponseTeamApplicationDTO {
	private Long id;

    private Long teamId;
    private Long userId;
    private String username;

    private String message;
    private ApplicationStatus status;

    private LocalDateTime createdAt;
    
    public ResponseTeamApplicationDTO from(TeamApplication application) {
        return ResponseTeamApplicationDTO.builder()
                .id(application.getId())
                .teamId(application.getTeam().getId())
                .userId(application.getUser().getId())
                .username(application.getUser().getUsername())
                .message(application.getMessage())
                .status(application.getStatus())
                .createdAt(application.getCreatedAt())
                .build();
    }
}
