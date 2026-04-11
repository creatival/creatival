package com.creatival.content.DTO;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.creatival.content.Content;
import com.creatival.content.ContentFile;
import com.creatival.content.Enum.OwnerType;
import com.creatival.content.Enum.Visibility;
import com.creatival.tag.ResponseTagDTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class UpdateVideoDTO {
	private Long id;
	
	@NotEmpty(message = "타이틀은 필수 사항입니다.")
	private String title;

	private String description;
	
	private Visibility visibility;

	private MultipartFile thumbnailFile;
	private String thumbnailFileUrl; // 기존 썸네일
	
	private MultipartFile videoFile;
	
	private String videoUrl; //기존 영상
	
	private boolean allowComment;
	
	
	public static UpdateVideoDTO from(Content content, ContentFile contentFile) {
		return UpdateVideoDTO.builder()
				.id(content.getId())
	            .title(content.getTitle())
	            .description(content.getDescription())
	            .visibility(content.getVisibility())
	            .thumbnailFileUrl(content.getThumbnailImgUrl())
	            .videoUrl(contentFile.getFileUrl())
	            .allowComment(content.isAllowComment())
	            .build();
	}
}
