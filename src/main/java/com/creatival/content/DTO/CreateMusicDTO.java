package com.creatival.content.DTO;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import com.creatival.content.Enum.OwnerType;
import com.creatival.content.Enum.Visibility;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@AllArgsConstructor
public class CreateMusicDTO {
	@NotEmpty(message = "타이틀은 필수 사항입니다.")
	private String title;

	private String description;
	
	private Visibility visibility;
	
	private OwnerType ownerType;

	private MultipartFile thumbnailFile;
	
	@NotNull(message = "음악 파일은 필수사항입니다.")
	private MultipartFile musicFile;
	
	private boolean allowComment;
	
	private String tagString;

	private boolean fanWork;

	private Long originalContentId;
	
	private String projectTag;
	
	private boolean paid;
	private BigDecimal price;
}
