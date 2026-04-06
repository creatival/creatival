package com.creatival.bookmark.dto;

import com.creatival.like.dto.LikeDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class ResponseBookmarkDTO {
	private boolean bookmarked;
	private long count;
	
	public static ResponseBookmarkDTO from(boolean bookmarked, long count) {
		return ResponseBookmarkDTO.builder()
				.bookmarked(bookmarked)
				.count(count)
				.build();
	}
}
