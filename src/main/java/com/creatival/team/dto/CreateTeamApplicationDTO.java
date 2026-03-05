package com.creatival.team.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.creatival.content.Enum.Visibility;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@AllArgsConstructor
public class CreateTeamApplicationDTO {
	@NotEmpty(message = "지원 메시지를 입력해주세요.")
    private String message;
}
