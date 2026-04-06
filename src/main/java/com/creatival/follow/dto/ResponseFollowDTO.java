package com.creatival.follow.dto;

import com.creatival.like.dto.LikeDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class ResponseFollowDTO {
	private boolean followed;
	private long count;
	public static ResponseFollowDTO from(boolean followed, long count) {
		return ResponseFollowDTO.builder()
				.followed(followed)
				.count(count)
				.build();
	}
}
