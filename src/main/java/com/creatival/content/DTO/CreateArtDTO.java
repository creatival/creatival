package com.creatival.content.DTO;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.creatival.content.Content;
import com.creatival.content.ContentFile;
import com.creatival.content.Enum.ContentType;
import com.creatival.content.Enum.OwnerType;
import com.creatival.content.Enum.Visibility;
import com.creatival.user.Users;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@AllArgsConstructor
public class CreateArtDTO {
	@NotEmpty
	private String title;
	
	private String description;
	
	private OwnerType ownerType;
	
	private String thumbnailImgUrl;
	
	private Visibility visibility;
	
	private boolean allowComment;
	private boolean fanWork;
	private List<MultipartFile> images;
	
	private Long originalContentId;
	
	public Content toEntity(Users user,String thumbnailImgUrl, Content originalContent) {
		return Content.builder()
				.title(this.title)
				.description(this.description)
				.ownerType(this.ownerType)
				.ThumbnailImgUrl(thumbnailImgUrl)
				.visibility(this.visibility)
				.isAllowComment(this.allowComment)
				.isFanWork(this.fanWork)
				.originalContent(originalContent)
				.build();
				
	}
}
