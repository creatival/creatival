package com.creatival.content;


import java.time.LocalDateTime;

import com.creatival.user.userRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Entity
public class ContentFile {
	
	public ContentFile() {
		// TODO Auto-generated constructor stub
	}
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(nullable = false)
	private String fileType;
	
	@Column(nullable = false)
	private String fileUrl;
	
	@Column(nullable = false)
	private int sortOrder;
	
	//UUID로 만들어진 이름
	@Column(nullable = false, unique = true)
	private String fileName;
	
	//원래 이름 
	@Column(nullable = false)
	private String originalFileName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "content_id")
	private Content content;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "episode_id")
	private Episode episode;
}
