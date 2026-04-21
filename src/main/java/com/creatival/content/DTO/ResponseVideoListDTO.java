package com.creatival.content.DTO;



import java.time.Duration;
import java.time.LocalDateTime;

import com.creatival.content.Content;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class ResponseVideoListDTO {
	private Long id;
	private String title;
	
	private Long viewCount;
	
	private Long userId;
	private String username;
	private String displayname;
	private String profileImgUrl;
	
	private String formattedDate;
	
	private String thumbnailUrl;
	
	private boolean paid;
	
	public static ResponseVideoListDTO from(Content content) {
		return ResponseVideoListDTO.builder()
				.id(content.getId())
				.title(content.getTitle())
				.viewCount(content.getViewCount())
				.userId(content.getUser().getId())
				.username(content.getUser().getUsername())
				.displayname(content.getUser().getDisplayName())
				.profileImgUrl(content.getUser().getProfileImgUrl())
				.thumbnailUrl(content.getThumbnailImgUrl())
				.formattedDate(formatRelativeTime(content.getCreatedAt()))
				.paid(content.isPaid())
				.build();
	}
	
	private static String formatRelativeTime(LocalDateTime createdAt) {
        if (createdAt == null) return "";
        
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(createdAt, now);
        long seconds = duration.getSeconds();

        if (seconds < 60) return "방금 전";
        if (seconds < 3600) return (seconds / 60) + "분 전";
        if (seconds < 86400) return (seconds / 3600) + "시간 전";
        if (seconds < 2592000) return (seconds / 86400) + "일 전";
        if (seconds < 31536000) return (seconds / 2592000) + "개월 전";
        return (seconds / 31536000) + "년 전";
    }
}