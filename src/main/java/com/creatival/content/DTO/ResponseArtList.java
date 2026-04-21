package com.creatival.content.DTO;

import com.creatival.content.Content;
import com.creatival.content.ContentFile;
import com.creatival.user.Users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class ResponseArtList {
	private Long id;
	private String title;
	
	private Long viewCount;
	private Long likeCount;
	
	private Long userId;
	private String username;
	private String displayname;
	
	private String thumbnailUrl; //contentFile의 sortOrder가 1인 것
	
	private boolean paid;
	
	public static ResponseArtList from(Content content, ContentFile contentFile) {
		return ResponseArtList.builder()
				.id(content.getId())
				.title(content.getTitle())
				.viewCount(content.getViewCount())
				.likeCount(content.getLikeCount())
				.userId(content.getUser().getId())
				.username(content.getUser().getUsername())
				.displayname(content.getUser().getDisplayName())
				.thumbnailUrl(contentFile.getFileUrl())
				.paid(content.isPaid())
				.build();
	}
}
