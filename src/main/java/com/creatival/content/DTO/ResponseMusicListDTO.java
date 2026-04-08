package com.creatival.content.DTO;

import com.creatival.content.Content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class ResponseMusicListDTO {
	private Long id;
	private String title;
	
	private Long userId;
	private String username;
	private String displayname;
	
	private String projectName;
	
	private String thumbnailUrl;
	
	public static ResponseMusicListDTO from(Content content) {
		ResponseMusicListDTO dto = new ResponseMusicListDTO();
		dto.setId(content.getId());
		dto.setTitle(content.getTitle());
		dto.setUserId(content.getUser().getId());
		dto.setUsername(content.getUser().getUsername());
		dto.setDisplayname(content.getUser().getDisplayName());
		dto.setThumbnailUrl(content.getThumbnailImgUrl());
		
		if(content.getProject()!=null) {
			dto.setProjectName(content.getProject().getTitle());
		}
		
		return dto;
	}
}
