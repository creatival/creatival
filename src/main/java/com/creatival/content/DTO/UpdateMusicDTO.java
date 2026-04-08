package com.creatival.content.DTO;

import org.springframework.web.multipart.MultipartFile;

import com.creatival.content.Content;
import com.creatival.content.ContentFile;
import com.creatival.content.Enum.Visibility;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class UpdateMusicDTO {
private Long id;
	
	@NotEmpty(message = "타이틀은 필수 사항입니다.")
	private String title;

	private String description;
	
	private Visibility visibility;

	private MultipartFile thumbnailFile;
	private String thumbnailFileUrl; // 기존 썸네일
	
	private MultipartFile musicFile;
	
	private String musicUrl; //기존 영상
	
	private boolean allowComment;
	
	
	public static UpdateMusicDTO from(Content content, ContentFile contentFile) {
		return UpdateMusicDTO.builder()
				.id(content.getId())
	            .title(content.getTitle())
	            .description(content.getDescription())
	            .visibility(content.getVisibility())
	            .thumbnailFileUrl(content.getThumbnailImgUrl())
	            .musicUrl(contentFile.getFileUrl())
	            .allowComment(content.isAllowComment())
	            .build();
	}
}
