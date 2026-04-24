package com.creatival.content.DTO;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import com.creatival.content.Enum.OwnerType;
import com.creatival.content.Enum.Visibility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@AllArgsConstructor
public class CreateFileDTO {
	private String title;
	private String description;
	private MultipartFile file;
	private MultipartFile extraImg;
	private String tagList;
	private boolean allowComment;
	private Visibility visibility=Visibility.PUBLIC;
	private OwnerType ownerType=OwnerType.USER;
	private boolean fanWork;

	private Long originalContentId;
	
	private String projectTag;
	
	private boolean paid;
	private BigDecimal price;
}
