package com.creatival.content.DTO;

import org.springframework.web.multipart.MultipartFile;

import com.creatival.content.Content;
import com.creatival.content.Series;
import com.creatival.content.Enum.OwnerType;
import com.creatival.content.Enum.Visibility;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class UpdateComicDTO {
	
	private Long id;
	private String title;
	
	private String description;
	
	private Long originalContentId;
	
	private MultipartFile thumbnailFile;
	
	private String thumbnailFileUrl; //기존의 Url
	
	private Visibility visibility;
	
	private boolean allowComment;
	
	private boolean end;
	
	public static UpdateComicDTO from(Content content, Series series) {
		UpdateComicDTO dto = UpdateComicDTO.builder()
				.id(content.getId())
				.title(content.getTitle())
				.description(content.getDescription())
				.thumbnailFileUrl(content.getThumbnailImgUrl())
				.visibility(content.getVisibility())
				.allowComment(content.isAllowComment())
				.end(series.isEnd())
				.build();
		if(content.getOriginalContent()!=null) {
			dto.setOriginalContentId(content.getOriginalContent().getId());
		}
		return dto;
	}
}
