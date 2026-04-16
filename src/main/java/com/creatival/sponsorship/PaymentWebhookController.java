package com.creatival.sponsorship;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.creatival.sponsorship.dto.PaymentRequestDTO;
import com.creatival.sponsorship.dto.WebhookDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/webhook")
public class PaymentWebhookController {

    private final SponsorshipService sponsorshipService;

    @PostMapping("/payment")
    public ResponseEntity<?> handlePaymentWebhook(@RequestBody WebhookDTO webhook) {

        if (!"Transaction.Paid".equals(webhook.getType())) {
            return ResponseEntity.ok("IGNORED");
        }

        sponsorshipService.completeSponsorship(
                webhook.getData().getPaymentId(),
                webhook.getData().getPaymentId() // 지금은 동일
        );

        return ResponseEntity.ok("OK");
    }

    @PostMapping("/complete")
    public ResponseEntity<?> complete(@RequestBody PaymentRequestDTO request) {
        sponsorshipService.completeSponsorship(
                request.getPaymentId(),
                request.getMerchantUid()
        );
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/admin/payment/retry")
    public ResponseEntity<?> retryPayment(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("merchantUid") String merchantUid) {

        sponsorshipService.completeSponsorship(paymentId, merchantUid);
        return ResponseEntity.ok("RETRY OK");
    }
    
    
}