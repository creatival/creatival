package com.creatival.team.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import com.creatival.content.Enum.Visibility;
import com.creatival.team.Team;
import com.creatival.team.Enum.TeamStatus;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class UpdateTeamDTO {

	@NotBlank(message = "팀 이름을 입력해주세요.")
    @Size(max = 50, message = "팀 이름은 50자 이하로 작성해주세요.")
    private String name;

    @NotBlank(message = "팀 설명을 입력해주세요.")
    @Size(max = 2000, message = "팀 설명은 2000자 이하로 작성해주세요.")
    private String description;
    
    private String profileImgUrl;
    
    private String bannerImgUrl;

    private MultipartFile profileImg;

    private MultipartFile bannerImg;

    private TeamStatus status;

    private Visibility visibility;
    
    public static UpdateTeamDTO from(Team team) {
    	return UpdateTeamDTO.builder()
    			.name(team.getName())
    			.description(team.getDescription())
    			.profileImgUrl(team.getProfileImgUrl())
    			.bannerImgUrl(team.getBannerImgUrl())
    			.status(team.getStatus())
    			.visibility(team.getVisibility())
    			.build();
    }
}