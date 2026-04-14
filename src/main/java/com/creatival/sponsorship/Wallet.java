package com.creatival.sponsorship;

import java.math.BigDecimal;

import com.creatival.like.TargetType;
import com.creatival.team.Team;
import com.creatival.user.Users;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Builder
@NoArgsConstructor
@Table(
	    uniqueConstraints = {
	        @UniqueConstraint(columnNames = {"ownerType", "ownerId"})
	    }
	)
public class Wallet {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private TargetType ownerType;
	
	private Long ownerId;

	@Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO; // 누적 후원액
	@Builder.Default
    private BigDecimal currentBalance = BigDecimal.ZERO; // 출금 가능 잔액

    // 후원 성공 시 잔액 추가 (동시성 제어 필요)
    public void addBalance(BigDecimal amount) {
    	this.totalAmount = this.totalAmount.add(amount);
        this.currentBalance = this.currentBalance.add(amount);
    }
}
