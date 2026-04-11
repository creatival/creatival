package com.creatival.content.DTO;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.creatival.content.Content;
import com.creatival.content.ContentFile;
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
public class UpdateFileDTO {
	private Long id;
	private String title;
	private String description;
	private MultipartFile file;
	private String fileUrl;
	private MultipartFile extraImg;
	private String extraImgUrl;
	private String extraImgName;
	private boolean allowComment;
	private Visibility visibility;
	
	public static UpdateFileDTO from(Content content, ContentFile file) {
		return UpdateFileDTO.builder()
				.id(content.getId())
				.title(content.getTitle())
				.description(content.getDescription())
				.fileUrl(file.getFileUrl())
				.allowComment(content.isAllowComment())
				.visibility(content.getVisibility())
				.build();
	}
	
	public static UpdateFileDTO from(Content content, ContentFile file, ContentFile extraImg) {
		return UpdateFileDTO.builder()
				.id(content.getId())
				.title(content.getTitle())
				.description(content.getDescription())
				.fileUrl(file.getFileUrl())
				.allowComment(content.isAllowComment())
				.visibility(content.getVisibility())
				.extraImgUrl(extraImg.getFileUrl())
				.extraImgName(extraImg.getOriginalFileName())
				.build();
	}
}
