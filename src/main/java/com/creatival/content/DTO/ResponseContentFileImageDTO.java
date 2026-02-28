package com.creatival.content.DTO;

import java.time.LocalDateTime;

import com.creatival.content.ContentFile;
import com.creatival.content.Enum.ContentType;
import com.creatival.content.Enum.OwnerType;
import com.creatival.content.Enum.Visibility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class ResponseContentFileImageDTO {
	private Long id;
	private String fileUrl;
	private int sortOrder;
	
	public static ResponseContentFileImageDTO from(ContentFile file) {
		return ResponseContentFileImageDTO.builder()
				.id(file.getId())
				.fileUrl(file.getFileUrl())
				.sortOrder(file.getSortOrder())
				.build();
	}
}
