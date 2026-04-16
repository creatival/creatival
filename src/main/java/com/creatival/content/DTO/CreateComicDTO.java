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
public class CreateComicDTO {
	//content 필드
	@NotEmpty(message = "타이틀은 필수 사항입니다.")
	private String title;

	private String description;

	private Visibility visibility;

	private OwnerType ownerType;

	private MultipartFile thumbnailFile;

	private boolean allowComment;
	
	private String tagString;

	private boolean fanWork;

	private Long originalContentId;
	
	private String projectTag;

	//series 관련 필드
	private boolean end;
}
