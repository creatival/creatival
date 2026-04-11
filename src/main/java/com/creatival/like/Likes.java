package com.creatival.like;



import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.creatival.board.Board;
import com.creatival.content.Content;
import com.creatival.content.Episode;
import com.creatival.tag.Tag;
import com.creatival.team.Project;
import com.creatival.team.Team;
import com.creatival.user.Users;

import groovy.transform.Generated;
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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(uniqueConstraints = {
	    @UniqueConstraint(columnNames = {"user_id", "target_id", "target_type"})
	})
public class Likes {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Users user;
	
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@Column(nullable = false)
	private Long targetId;
	
	@Enumerated(EnumType.STRING)
	private TargetType targetType;
	
	
}
