package com.creatival.content.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.creatival.content.ContentFile;
import com.creatival.content.Episode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class ResponseComicEpisodeDetail {
	private Long id;
	
	private Integer episodeNum;
	
	private String title;
	
	private List<ResponseContentFileImageDTO> fileList;
	
	boolean free;
	
	private String note;
	
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	private Long likeCount;
	private Long viewCount;
	
	private BigDecimal price;
	
	private String username;
	
	public static ResponseComicEpisodeDetail from(Episode episode, List<ContentFile> files) {
		return ResponseComicEpisodeDetail.builder()
				.id(episode.getId())
				.episodeNum(episode.getEpisodeNum())
				.title(episode.getTitle())
				.fileList(files.stream().map(file -> ResponseContentFileImageDTO.from(file)).toList())
				.note(episode.getNote())
				.createdAt(episode.getCreatedAt())
				.updatedAt(episode.getUpdatedAt())
				.free(episode.isFree())
				.likeCount(episode.getLikeCount())
				.viewCount(episode.getViewCount())
				.price(episode.getPrice())
				.username(episode.getSeries().getContent().getUser().getUsername())
				.build();
	}
}
