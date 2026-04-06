package com.creatival.like.dto;

import com.creatival.like.Likes;
import com.creatival.tag.ResponseTagDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class LikeDTO {
	private boolean liked;
	private Long count;
	
	public static LikeDTO from(boolean liked, long count) {
		return LikeDTO.builder()
				.liked(liked)
				.count(count)
				.build();
	}
	
}
