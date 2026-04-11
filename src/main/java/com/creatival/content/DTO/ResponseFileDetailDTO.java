package com.creatival.content.DTO;

import java.time.LocalDateTime;

import com.creatival.content.Content;
import com.creatival.content.ContentFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class ResponseFileDetailDTO {
	private Long id;
	private String title;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String description;
	
	private Long viewCount;
	
	private ResponseContentFileImageDTO preview;
	private ResponseContentFileDTO file;
	
	private String username;
	private String displayname;
	
	private boolean allowComment;
	
	public static ResponseFileDetailDTO from(Content content, ContentFile file) {
		ResponseFileDetailDTO dto = new ResponseFileDetailDTO();
		dto.setId(content.getId());
		dto.setTitle(content.getTitle());
		dto.setCreatedAt(content.getCreatedAt());
		dto.setUpdatedAt(content.getUpdatedAt());
		dto.setDescription(content.getDescription());
		dto.setViewCount(content.getViewCount());
		dto.setUsername(content.getUser().getUsername());
		dto.setDisplayname(content.getUser().getDisplayName());
		dto.setFile(ResponseContentFileDTO.from(file));
		dto.setAllowComment(content.isAllowComment());
		
		return dto;
	}
	
	public static ResponseFileDetailDTO from(Content content, ContentFile file, ContentFile previewImg) {
		ResponseFileDetailDTO dto = new ResponseFileDetailDTO();
		dto.setId(content.getId());
		dto.setTitle(content.getTitle());
		dto.setCreatedAt(content.getCreatedAt());
		dto.setUpdatedAt(content.getUpdatedAt());
		dto.setDescription(content.getDescription());
		dto.setViewCount(content.getViewCount());
		dto.setUsername(content.getUser().getUsername());
		dto.setDisplayname(content.getUser().getDisplayName());
		dto.setFile(ResponseContentFileDTO.from(file));
		dto.setPreview(ResponseContentFileImageDTO.from(previewImg));
		dto.setAllowComment(content.isAllowComment());
		
		return dto;
	}
}
