
package com.creatival.content.DTO;

import java.time.LocalDateTime;

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
	private boolean AllowComment;
	
	
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
	private int totalEpisode;
	
	//2차 창작 여부
	private String originalContentId;
	private String originalContentName;
}
