package com.creatival.content.DTO;

import com.creatival.content.ContentFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class ResponseContentFileVideoDTO {
	private Long id;
	private String fileUrl;
	
	public static ResponseContentFileVideoDTO from(ContentFile contentFile) {
		return ResponseContentFileVideoDTO.builder()
				.id(contentFile.getId())
				.fileUrl(contentFile.getFileUrl())
				.build();
	}
}
