package com.creatival.tag;

import com.creatival.content.DTO.UpdateNovelEpisodeDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class ResponseTagDTO {
	private Long id;
	
	private String tagText;
	
	public static ResponseTagDTO from(Tag tag) {
		return builder()
				.id(tag.getId())
				.tagText(tag.getTagText())
				.build();
	}
}
