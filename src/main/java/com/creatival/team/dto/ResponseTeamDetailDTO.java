package com.creatival.team.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.creatival.content.Enum.Visibility;
import com.creatival.team.Team;
import com.creatival.team.Enum.TeamStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseTeamDetailDTO {

    private Long id;

    private String name;

    private String description;

    private String profileImgUrl;

    private String bannerImgUrl;

    private TeamStatus status;

    private Visibility visibility;

    private LocalDateTime createdAt;

    // 리더 정보
    private Long leaderId;
    private String leaderUsername;
    private String leaderProfileImgUrl;

    public static ResponseTeamDetailDTO from(Team team) {

        return ResponseTeamDetailDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .profileImgUrl(team.getProfileImgUrl())
                .bannerImgUrl(team.getBannerImgUrl())
                .status(team.getStatus())
                .visibility(team.getVisibility())
                .createdAt(team.getCreatedAt())
                .leaderId(team.getUser().getId())
                .leaderUsername(team.getUser().getUsername())
                .leaderProfileImgUrl(team.getUser().getProfileImgUrl())
                .build();
    }
}