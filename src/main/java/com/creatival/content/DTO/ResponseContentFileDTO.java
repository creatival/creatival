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
public class ResponseContentFileDTO {
	private Long id;
	private String fileUrl;
	private String originalFileName;
	
	public static ResponseContentFileDTO from(ContentFile contentFile) {
		return ResponseContentFileDTO.builder()
				.id(contentFile.getId())
				.fileUrl(contentFile.getFileUrl())
				.originalFileName(contentFile.getOriginalFileName())
				.build();
	}
}
