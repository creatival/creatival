package com.creatival.content;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.UniqueElements;

import com.creatival.content.Enum.OwnerType;
import com.creatival.content.Enum.Visibility;
import com.creatival.user.UserDTO;
import com.creatival.user.Users;
import com.creatival.user.userRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
public class Content {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Users user; 
	
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "team_id")
//	private Team team; 
//	
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "project_id")
//	private Project project; 
	
	@Enumerated(EnumType.STRING)
	private OwnerType onwerType;
	
	@Column(nullable = false)
	private String type;
	
	@Column(nullable = false)
	private String title;
	
	@Column(nullable = false)
	private String description;
	
	private String ThumbanilImgUrl;
	
	@Column(nullable = false)
	private Visibility visibility;
	
	private boolean isAllowComment;
	private boolean isFanWork;
	
	@Builder.Default
	@Column(nullable = false)
	private Long viewCount=0L;
	
	@CreationTimestamp
	@Column(nullable = false)
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "original_content_id")
	private Content originalContent;
	
}
