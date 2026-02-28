package com.creatival.content.DTO;

import com.creatival.content.Content;
import com.creatival.content.Episode;
import com.creatival.content.Enum.ContentType;
import com.creatival.content.Enum.OwnerType;
import com.creatival.content.Enum.Visibility;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@AllArgsConstructor
public class CreateContentFileDTO {
    private Long id;
    
	private String fileType;
	
	private String fileUrl;
	
	private int sortOrder;
	
	private String fileName;
	
	private String originalFileName;

	private String contentId;
	
	private String episodeId;
}
