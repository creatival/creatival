package com.creatival.content.DTO;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.creatival.content.ContentFile;
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
public class UpdateComicEpisodeDTO {
	
	private Long id;
	@NotEmpty(message = "타이틀은 필수 사항입니다.")
	private String title;
	
	//새로운 만화 내용들
	private List<MultipartFile> fileList;
	
	private List<ResponseContentFileImageDTO> images;
	
	private List<Long> deleteFileIds; // 삭제될 파일 id들
	
	boolean free;
	
	private String note;
	
	public static UpdateComicEpisodeDTO from(Episode episode, List<ContentFile> files) {
		return UpdateComicEpisodeDTO.builder()
				.id(episode.getId())
				.title(episode.getTitle())
				.free(episode.isFree())
				.note(episode.getNote())
				.images(files.stream().map(file -> ResponseContentFileImageDTO.from(file)).toList())
				.build();
				
	}
}
