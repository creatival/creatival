package com.creatival.content.DTO;

import java.math.BigDecimal;

import com.creatival.content.Episode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class UpdateNovelEpisodeDTO {
    private Long id;
	
	private Integer episodeNum;

	private String title;
	
	private String novelContent;
	
	boolean free;
	boolean deleted;
	
	private String note;
	
	private BigDecimal price;
	
	public static UpdateNovelEpisodeDTO from(Episode episode) {
		return UpdateNovelEpisodeDTO.builder()
				.id(episode.getId())
				.episodeNum(episode.getEpisodeNum())
				.title(episode.getTitle())
				.novelContent(episode.getNovelContent())
				.free(episode.isFree())
				.deleted(episode.isDeleted())
				.note(episode.getNote())
				.price(episode.getPrice())
				.build();
	}
}
