package com.creatival.team.DTO;


import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.creatival.content.Enum.Visibility;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@AllArgsConstructor
public class CreateTeamDTO {
	 @NotEmpty(message = "팀 이름은 필수입니다.")
    @Size(max = 50, message = "팀 이름은 50자 이내여야 합니다.")
    private String name;

    @Size(max = 1000, message = "설명은 1000자 이내여야 합니다.")
    private String description;

    private Visibility visibility;

    // 파일은 URL이 아니라 MultipartFile로 받는 게 맞음
    private MultipartFile profileImage;

    private MultipartFile bannerImage;
    
    private List<String> tags;
}
