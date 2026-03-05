package com.creatival.team;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.creatival.team.Enum.ApplicationStatus;
import com.creatival.team.Enum.TeamRole;
import com.creatival.user.Users;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class TeamApplication {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Team team;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Users user;
	
	@Enumerated(EnumType.STRING)
	private ApplicationStatus status;
	
	private String message; //지원 메세지
	
	@CreationTimestamp
	private LocalDateTime createdAt;
}
