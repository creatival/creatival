package com.creatival.team.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.creatival.content.Enum.Visibility;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@AllArgsConstructor
public class CreateProjectDTO {

	@NotEmpty(message = "제목은 필수 사항입니다.")
	@Size(max = 50, message = "제목은 50글자 이내로 작성해주셔야합니다.")
    private String title;

    private String description;

    private MultipartFile bannerImg;

    private LocalDateTime endDate;

    private Visibility visibility;
    
    @NotEmpty(message = "프로젝트 태그는 필수 사항입니다.")
    private String projectTag;
    
    private List<String> tags;

}