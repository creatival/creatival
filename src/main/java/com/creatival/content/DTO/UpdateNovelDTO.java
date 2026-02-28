package com.creatival.content.DTO;

import java.time.LocalDateTime;

import com.creatival.content.Content;
import com.creatival.content.Series;
import com.creatival.content.Enum.ContentType;
import com.creatival.content.Enum.OwnerType;
import com.creatival.content.Enum.Visibility;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class UpdateNovelDTO {
	@NotEmpty(message = "타이틀은 필수 사항입니다.")
	private String title;
	
	private String description;

	@Enumerated(EnumType.STRING)
	private Visibility visibility;

	private boolean allowComment;
	
	//Series 관련
	private boolean end;
	
	public static UpdateNovelDTO from(Content content, Series series) {
		return UpdateNovelDTO.builder()
				.title(content.getTitle())
				.description(content.getDescription())
				.visibility(content.getVisibility())
				.allowComment(content.isAllowComment())
				.end(series.isEnd())
				.build();
	}
}
