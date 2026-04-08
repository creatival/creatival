package com.creatival.content.DTO;

import com.creatival.content.Content;
import com.creatival.content.Enum.ContentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class ResponseContentListForProject {
	private Long id;
	private String title;
	private String thumbnailUrl;
	private String contentUrl;
	
	private String username;
	private String displayname;
	private String profileImgUrl;
	
	private ContentType type;
	private Long viewCount;
	private int likeCount;
	
	public static ResponseContentListForProject fromNovel(Content content) {
		return ResponseContentListForProject.builder()
				.id(content.getId())
				.title(content.getTitle())
				.thumbnailUrl(content.getThumbnailImgUrl())
				.contentUrl("/content/novel_detail/"+content.getId())
				.username(content.getUser().getUsername())
				.displayname(content.getUser().getDisplayName())
				.profileImgUrl(content.getUser().getProfileImgUrl())
				.viewCount(content.getViewCount())
				.type(content.getType())
				.likeCount(0) // 나중에 수정할 것
				.build();
	}
	public static ResponseContentListForProject fromArt(Content content, String thumbnailUrl) {
		return ResponseContentListForProject.builder()
				.id(content.getId())
				.title(content.getTitle())
				.thumbnailUrl(thumbnailUrl)
				.contentUrl("/content/art/detail/"+content.getId())
				.username(content.getUser().getUsername())
				.displayname(content.getUser().getDisplayName())
				.profileImgUrl(content.getUser().getProfileImgUrl())
				.viewCount(content.getViewCount())
				.type(content.getType())
				.likeCount(0) // 나중에 수정할 것
				.build();
	}
	
	
}
