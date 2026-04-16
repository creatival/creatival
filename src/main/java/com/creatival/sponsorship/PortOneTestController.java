package com.creatival.sponsorship;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.creatival.like.TargetType;
import com.creatival.sponsorship.dto.PortOnePaymentSummary;
import com.creatival.sponsorship.repository.SponsorshipRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PortOneTestController {
    private final SponsorshipRepository sponsorshipRepository;


    private final PortOnePaymentService portOnePaymentService;

    @GetMapping("/test/portone/payment/{paymentId}")
    public PortOnePaymentSummary getPayment(@PathVariable String paymentId) {
        return portOnePaymentService.getPayment(paymentId);
    }
    
    @PostMapping("/api/sponsorship/create")
    public ResponseEntity<String> createSponsorship() {
        String merchantUid = "order_test_" + System.currentTimeMillis();

        sponsorshipRepository.save(
            Sponsorship.builder()
                .merchantUid(merchantUid)
                .amount(BigDecimal.valueOf(1000))
                .status(SponsorshipStatus.PENDING)
                .targetType(TargetType.TEAM)
                .targetId(1L)
                .build()
        );

        return ResponseEntity.ok(merchantUid);
    }
}