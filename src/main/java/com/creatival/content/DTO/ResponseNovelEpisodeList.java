package com.creatival.content.DTO;

import java.time.LocalDateTime;

import com.creatival.content.Episode;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class ResponseNovelEpisodeList {
	@NotEmpty(message = "타이틀은 필수 사항입니다.")
	private String title;
	
	boolean free;
	
	private Integer episodeNum;
	
	boolean deleted;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
	public static ResponseNovelEpisodeList from(Episode episode) {
		return ResponseNovelEpisodeList.builder()
				.title(episode.getTitle())
				.free(episode.isFree())
				.episodeNum(episode.getEpisodeNum())
				.deleted(episode.isDeleted())
				.createdAt(episode.getCreatedAt())
				.updatedAt(episode.getUpdatedAt())
				.build();
	}
}
