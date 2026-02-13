package com.creatival.content;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class Episode {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "series_id")
	private Series series;
	
	@Column(nullable = false)
	private Integer episodeNum;
	
	@Column(nullable = false)
	private String title;
	
	@Lob
	private String novelContent;
	
	boolean isFree=true;
	boolean isDeleted=false;
	
	@Lob
	private String note;
	
	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedAt;
	
	
}
