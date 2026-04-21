package com.creatival.redeemcode;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.creatival.like.TargetType;
import com.creatival.user.Users;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "redeem_code", uniqueConstraints = {
        @UniqueConstraint(columnNames = "code")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedeemCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TargetType targetType;

    @Column(nullable = false)
    private Long targetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issuer_id", nullable = false)
    private Users issuer;

    @Column(nullable = false)
    private boolean used;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "used_by_id")
    private Users usedBy;

    private LocalDateTime usedAt;

    private LocalDateTime expiredAt;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}