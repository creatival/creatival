package com.creatival.sponsorship;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.creatival.content.Enum.Visibility;
import com.creatival.like.TargetType;
import com.creatival.tag.TagToTeam;
import com.creatival.team.Project;
import com.creatival.team.Team;
import com.creatival.team.TeamApplication;
import com.creatival.team.TeamMember;
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
import jakarta.persistence.OneToOne;
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
public class Sponsorship {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String merchantUid;
	
	@Column(unique = true)
	private String paymentId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Users user;
	
	@Enumerated(EnumType.STRING)
	private TargetType targetType;
	
	@Column(nullable = false)
	private Long targetId;
	
	@Column(nullable = false)
	private BigDecimal amount; // 후원 구액
	
	@Enumerated(EnumType.STRING)
	private SponsorshipStatus status;
	
	@OneToOne(mappedBy = "sponsorship", cascade = CascadeType.ALL)
	private Payment payment;
	
	public void complete() {
        this.status = SponsorshipStatus.PAID;
    }

	public boolean isPaid() {
        return this.status == SponsorshipStatus.PAID;
    }
	
}
