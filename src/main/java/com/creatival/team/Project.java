package com.creatival.team;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.creatival.content.Content;
import com.creatival.content.Enum.Visibility;
import com.creatival.tag.TagToProject;
import com.creatival.tag.TagToTeam;
import com.creatival.team.Enum.TeamStatus;
import com.creatival.user.Users;

import jakarta.persistence.CascadeType;
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
@NoArgsConstructor
public class Project {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String title;
	
	@Column(columnDefinition = "TEXT")
	private String description; 
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private TeamStatus status=TeamStatus.ACTIVITY;
	
	@Column(nullable = false)
	@Builder.Default
	private int totalProgress=0;
	
	private String bannerImgUrl;
	
	@Column(nullable = false, unique = true)
	private String projectTag;
	
	@CreationTimestamp
	@Column(nullable = false)
	private LocalDateTime createdAt;
	
	private LocalDateTime endDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Visibility visibility;
	
	@Builder.Default
	private Long likeCount=0L;
	
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<TagToProject> tagToProject = new ArrayList<>();
	
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Content> contents = new ArrayList<>();
	
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Checkout> checkouts = new ArrayList<>();
	
	@Column(nullable = false)
	private boolean supportEnabled = true;

	@Column
	private BigDecimal goalAmount;
}
