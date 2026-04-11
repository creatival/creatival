package com.creatival.team;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;
import org.hibernate.annotations.Where;

import com.creatival.content.Content;
import com.creatival.content.Enum.Visibility;
import com.creatival.tag.TagToProject;
import com.creatival.team.Enum.TeamStatus;
import com.creatival.user.Users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@SoftDelete(strategy = SoftDeleteType.DELETED)
public class History {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	private Project project;
	
	//작성자
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Users user;
	
	@Column(nullable = false)
	private String versionTag;
	
	private String title;
	
	//내용
	@Column(columnDefinition = "TEXT")
	private String text;
	
	@CreationTimestamp
	private LocalDateTime createdAt;
	

}
