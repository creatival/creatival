package com.creatival.follow;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.creatival.like.Likes;
import com.creatival.like.TargetType;
import com.creatival.user.Users;

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
public class Follow {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Users user;
	
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@Enumerated(EnumType.STRING)
	private TargetType targetType; //User or Team or Project
	
	@Column(nullable = false)
	private long targetId;
}
