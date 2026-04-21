package com.creatival.content.DTO;

import java.time.LocalDateTime;

import com.creatival.content.Content;
import com.creatival.content.ContentFile;
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
public class ResponseMusicDetailDTO {
	private Long id;
	private String title;
	private String description;
	
	private Long viewCount;
	
	private Long userId;
	private String username;
	private String displayname;
	
	private String ThumbnailUrl;
	
	private OwnerType ownerType;
	
	private Visibility visibility;
	
	private boolean isAllowComment;
	private boolean isFanWork;
	
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	private ResponseContentFileMusicDTO music;
	
	private boolean paid;
	
	public static ResponseMusicDetailDTO from(Content content, ContentFile contentFile) {
		return ResponseMusicDetailDTO.builder()
				.id(content.getId())
				.title(content.getTitle())
				.description(content.getDescription())
				.viewCount(content.getViewCount())
				.ThumbnailUrl(content.getThumbnailImgUrl())
				.userId(content.getUser().getId())
				.username(content.getUser().getUsername())
				.displayname(content.getUser().getDisplayName())
				.ownerType(content.getOwnerType())
				.visibility(content.getVisibility())
				.isAllowComment(content.isAllowComment())
				.isFanWork(content.isFanWork())
				.createdAt(content.getCreatedAt())
				.updatedAt(content.getUpdatedAt())
				.music(ResponseContentFileMusicDTO.from(contentFile))
				.paid(content.isPaid())
				.build();
	}
}
