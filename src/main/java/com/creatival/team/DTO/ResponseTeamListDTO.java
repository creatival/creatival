package com.creatival.team.DTO;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.creatival.content.Enum.Visibility;
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
public class ResponseTeamListDTO {
	private Long id;

    private String name;

    private String profileImgUrl;
    
    private String bannerImgUrl;

    //일부 설명만 가져옴
    private String description;

    private String leaderUsername;

    private TeamStatus status;

    private Visibility visibility;
    
    public static ResponseTeamListDTO from(Team team) {

        String preview = team.getDescription();
        if (preview != null && preview.length() > 100) {
            preview = preview.substring(0, 100) + "...";
        }

        return ResponseTeamListDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .profileImgUrl(team.getProfileImgUrl())
                .bannerImgUrl(team.getBannerImgUrl())
                .description(preview)
                .leaderUsername(team.getUser().getUsername())
                .status(team.getStatus())
                .visibility(team.getVisibility())
                .build();
    }
}
