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
public class ResponseNovelList {
	//content 필드
    private Long id;
	private String title;
	private ContentType contentType;
	
	
	
	private OwnerType ownerType;
	
	//권한 관련
	private Visibility visibility;
	
	private Long viewCount;
	
	//User 관련
	private Long userId;
	private String username;
	private String displayname;
	
	private String thumbnailUrl;
	
	private boolean fanWork;
	
	private LocalDateTime createdAt;
	
	//series 관련 필드
	private boolean end;
	
	public static ResponseNovelList from(Content content, Series series) {
		return ResponseNovelList.builder()
				.id(content.getId())
				.title(content.getTitle())
				.contentType(content.getType())
				.ownerType(content.getOnwerType())
				.visibility(content.getVisibility())
				.viewCount(content.getViewCount())
				.userId(content.getUser().getId())
				.username(content.getUser().getUsername())
				.displayname(content.getUser().getDisplayName())
				.thumbnailUrl(content.getThumbnailImgUrl())
				.fanWork(content.isFanWork())
				.createdAt(content.getCreatedAt())
				.end(series != null ? series.isEnd() : false)
				.build();
	}
}
