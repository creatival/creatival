package com.creatival.team.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.creatival.content.Enum.Visibility;
import com.creatival.team.Project;
import com.creatival.team.Enum.TeamStatus;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class UpdateProjectDTO {
	private Long id;
	
	@NotEmpty(message = "제목은 필수 사항입니다.")
	@Size(max = 50, message = "제목은 50글자 이내로 작성해주셔야합니다.")
    private String title;

    private String description;

    private MultipartFile bannerImg;

    private LocalDateTime endDate;

    private Visibility visibility;
    
    private TeamStatus status;
    
    private String bannerImgUrl; // 업데이트 전이미지
    
    public static UpdateProjectDTO from(Project project) {
    	return UpdateProjectDTO.builder()
    			.id(project.getId())
    			.title(project.getTitle())
    			.description(project.getDescription())
    			.endDate(project.getEndDate())
    			.visibility(project.getVisibility())
    			.status(project.getStatus())
    			.bannerImgUrl(project.getBannerImgUrl())
    			.build();
    }
}
