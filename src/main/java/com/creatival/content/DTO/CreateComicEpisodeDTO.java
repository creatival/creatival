package com.creatival.content.DTO;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@AllArgsConstructor
public class CreateComicEpisodeDTO {
	@NotEmpty(message = "타이틀은 필수 사항입니다.")
	private String title;
	
	private List<MultipartFile> fileList;
	
	boolean free;
	
	private String note;
}
