package com.creatival.content.DTO;

import org.springframework.web.multipart.MultipartFile;

import com.creatival.content.Enum.OwnerType;
import com.creatival.content.Enum.Visibility;
import com.creatival.team.Project;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@AllArgsConstructor
public class CreateNovelDTO {
	//content 필드
	@NotEmpty(message = "타이틀은 필수 사항입니다.")
	private String title;

	private String description;

	@NotNull(message = "공개 범위를 꼭 선택해주십시오")
	private Visibility visibility = Visibility.PUBLIC;
	
	@NotNull(message = "소유자 유형을 반드시 선택해주십시오")
	private OwnerType ownerType = OwnerType.USER;

	private MultipartFile thumbnailFile;

	private boolean allowComment;
	
	private String tagString;

	private boolean fanWork;

	private Long originalContentId;
	
	private String projectTag;

	//series 관련 필드
	private boolean end;
}
