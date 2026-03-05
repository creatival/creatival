package com.creatival.content.DTO;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.creatival.content.Content;
import com.creatival.content.Enum.Visibility;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class UpdateArtDTO {
	private String title;
	
	private String description;

	@Enumerated(EnumType.STRING)
	private Visibility visibility;

	private boolean allowComment;
	
	private List<MultipartFile> newFiles;
	
	private List<Long> deleteFileIds;
	
	public static UpdateArtDTO from(Content content) {
		return UpdateArtDTO.builder()
				.title(content.getTitle())
				.description(content.getDescription())
				.visibility(content.getVisibility())
				.allowComment(content.isAllowComment())
				.build();
	}
}
