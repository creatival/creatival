package com.creatival.content.DTO;

import org.springframework.web.multipart.MultipartFile;

import com.creatival.content.Enum.OwnerType;
import com.creatival.content.Enum.Visibility;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@AllArgsConstructor
public class CreateNovelEpisodeDTO {
	@NotEmpty(message = "타이틀은 필수 사항입니다.")
	private String title;
	
	@NotEmpty(message = "내용은 필수적으로 채워주셔야합니다.")
	private String novelContent;
	
	boolean free;
	
	private String note;
}
