package com.creatival.team;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.creatival.content.Enum.Visibility;
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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class Team {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//리더(팀의 소유자)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Users user;
	
	@Column(nullable = false)
	private String name;
	
	@Column(columnDefinition = "TEXT")
	private String description;
	
	private String profileImgUrl;
	private String bannerImgUrl;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private TeamStatus status=TeamStatus.ACTIVITY;
	
	@CreationTimestamp
	@Column(nullable = false)
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	@Column(nullable = true)
	private LocalDateTime updatedAt;
	
	@Builder.Default
	private Long likeCount=0L;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private Visibility visibility=Visibility.PUBLIC;
	
	@OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<TagToTeam> tagToTeam = new ArrayList<>();
	
	@OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<TeamMember> teamMember = new ArrayList<>();
	
	@OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<TeamApplication> teamApplications = new ArrayList<>();
	
	@OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Project> teamProjects = new ArrayList<>();
	
	@Column(nullable = false)
	private boolean supportEnabled = true;

	@Column
	private BigDecimal goalAmount;
}
