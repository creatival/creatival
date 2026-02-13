package com.creatival.content;

import org.springframework.web.multipart.MultipartFile;

import com.creatival.content.Enum.OwnerType;
import com.creatival.content.Enum.Visibility;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//모든 창작물 등록의 DTO를 담당합니다.
public class ContentCreateDTO {
	
	@NoArgsConstructor
	@Getter @Setter
	@AllArgsConstructor
	public static class createNovelDTO {

		
		//content 필드
		@NotEmpty(message = "타이틀은 필수 사항입니다.")
		private String title;
		
		private String description;
		
		private Visibility visibility;
		
		private OwnerType ownerType;
		
		private MultipartFile thumbnailFile;
		
		private boolean allowComment;
		private boolean fanWork;
		private Long originalContentId;
		
		//series 관련 필드
		private boolean end;
	}
	
}
