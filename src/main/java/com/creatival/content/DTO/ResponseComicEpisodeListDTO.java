package com.creatival.content.DTO;

import java.time.LocalDateTime;

import com.creatival.content.ContentFile;
import com.creatival.content.Episode;
import com.creatival.content.Enum.ContentType;
import com.creatival.content.Enum.OwnerType;
import com.creatival.content.Enum.Visibility;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class ResponseComicEpisodeListDTO {
	
	private Long id;
	
	@NotEmpty(message = "타이틀은 필수 사항입니다.")
	private String title;
	
	private boolean free;
	
	private Integer episodeNum;
	
	private boolean deleted;
	
	private String username;
	
	private Long viewCount;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
	private String thumbnailUrl;
	
	private Long likeCount;
	
	public static ResponseComicEpisodeListDTO from(Episode episode, ContentFile contentFile) {
		ResponseComicEpisodeListDTO dto = ResponseComicEpisodeListDTO.builder()
				.id(episode.getId())
				.title(episode.getTitle())
				.free(episode.isFree())
				.episodeNum(episode.getEpisodeNum())
				.viewCount(episode.getViewCount())
				.deleted(episode.isDeleted())
				.createdAt(episode.getCreatedAt())
				.updatedAt(episode.getUpdatedAt())
				.username(episode.getSeries().getContent().getUser().getUsername())
				.likeCount(episode.getLikeCount())
				.build();
		if(contentFile!=null) {
			dto.setThumbnailUrl(contentFile.getFileUrl());
		}
		return dto;
	}
}
