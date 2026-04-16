package com.creatival.sponsorship;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
public class Payment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String paymentId; // 포트원 결제 고유 번호

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sponsorship_id")
    private Sponsorship sponsorship;

    private String payMethod; // card, trans, vbank 등
    private String pgProvider; // kg_inicis, kakaopay 등
    private String receiptUrl; // 매출전표 URL
    
    @Column(nullable = false)
    private OffsetDateTime paidAt; // 결제 시점
}
