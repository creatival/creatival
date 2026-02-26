package com.creatival.content.DTO;

import java.time.LocalDateTime;

import com.creatival.content.Episode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class ResponseNovelEpisodeDetail {
	private Long id;
	
	private Integer episodeNum;
	
	private String title;
	
	private String novelContent;
	
	boolean free;
	
	private String note;
	
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	public static ResponseNovelEpisodeDetail from(Episode episode) {
		return ResponseNovelEpisodeDetail.builder()
				.id(episode.getId())
				.episodeNum(episode.getEpisodeNum())
				.title(episode.getTitle())
				.novelContent(episode.getNovelContent())
				.free(episode.isFree())
				.note(episode.getNote())
				.createdAt(episode.getCreatedAt())
				.updatedAt(episode.getUpdatedAt())
				.build();
	}
}
