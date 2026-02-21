
package com.creatival.content.DTO;

import java.time.LocalDateTime;

import com.creatival.content.Content;
import com.creatival.content.Series;
import com.creatival.content.Enum.ContentType;
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
public class ResponseNovelDetail {
	//content 필드
    private Long id;
	private String title;
	private ContentType contentType;
	private String description;
	
	
	private OwnerType ownerType;
	
	//권한 관련
	private Visibility visibility;
	private boolean allowComment;
	
	
	private Long viewCount;
	
	//User 관련
	private Long userId;
	private String username;
	private String displayname;
	private String profileImgUrl;
	
	private String thumbnailUrl;
	
	private boolean fanWork;
	
	private LocalDateTime createdAt;
	
	//series 관련 필드
	private boolean end;
	private int totalEpisode;
	
	//2차 창작 여부
	private Long originalContentId;
	private String originalContentName;
	
	public static ResponseNovelDetail from(Content content) {
		return ResponseNovelDetail.builder()
				.id(content.getId())
				.title(content.getTitle())
				.contentType(content.getType())
				.description(content.getDescription())
				.ownerType(content.getOnwerType())
				.visibility(content.getVisibility())
				.allowComment(content.isAllowComment())
				.viewCount(content.getViewCount())
				.userId(content.getUser().getId())
				.username(content.getUser().getUsername())
				.displayname(content.getUser().getDisplayName())
				.profileImgUrl(content.getUser().getProfileImgUrl())
				.thumbnailUrl(content.getThumbnailImgUrl())
				.fanWork(content.isFanWork())
				.createdAt(content.getCreatedAt())
				.end(content.getSeries() != null ? content.getSeries().isEnd() : true)
				.totalEpisode(content.getSeries() != null ? content.getSeries().getTotalEpisode() : null)
				.originalContentId(content.getOriginalContent() != null ? content.getOriginalContent().getId() : null)
				.originalContentName(content.getOriginalContent() != null ? content.getOriginalContent().getTitle() : null)
				.build();
	}
}
