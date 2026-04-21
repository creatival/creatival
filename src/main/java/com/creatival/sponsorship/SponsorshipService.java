package com.creatival.sponsorship;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.creatival.like.TargetType;
import com.creatival.sponsorship.dto.PortOneTokenResponse;
import com.creatival.sponsorship.dto.ResponsePaymentDTO;
import com.creatival.sponsorship.dto.ResponseSupportHistoryListDTO;
import com.creatival.sponsorship.repository.PaymentRepository;
import com.creatival.sponsorship.repository.SponsorshipRepository;
import com.creatival.sponsorship.repository.WalletRepository;
import com.creatival.user.Users;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SponsorshipService {

    private final SponsorshipRepository sponsorshipRepository;
    private final PaymentRepository paymentRepository;
    private final WalletRepository walletRepository;

    @Value("${portone.api.secret}")
    private String apiSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    private String cachedAccessToken;
    private Instant tokenExpiresAt;

    private String getAccessToken() {
        if (cachedAccessToken != null
                && tokenExpiresAt != null
                && Instant.now().isBefore(tokenExpiresAt.minusSeconds(30))) {
            return cachedAccessToken;
        }

        String url = "https://api.portone.io/login/api-secret";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(
                Map.of("apiSecret", apiSecret.trim()),
                headers
        );

        try {
            ResponseEntity<PortOneTokenResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    PortOneTokenResponse.class
            );

            PortOneTokenResponse body = response.getBody();
            if (body == null || body.getAccessToken() == null || body.getAccessToken().isBlank()) {
                throw new IllegalStateException("PortOne access token 발급 실패");
            }

            cachedAccessToken = body.getAccessToken();

            // JWT exp 직접 파싱 안 하고 여유 있게 25분 캐시
            tokenExpiresAt = Instant.now().plusSeconds(25 * 60);

            return cachedAccessToken;
        } catch (HttpStatusCodeException e) {
            log.error("PortOne 토큰 발급 실패 status={}, body={}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new IllegalStateException("PortOne 토큰 발급 실패", e);
        }
    }

    public ResponsePaymentDTO getPayment(String paymentId) {
        String url = "https://api.portone.io/payments/" + paymentId;
        String accessToken = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<ResponsePaymentDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    ResponsePaymentDTO.class
            );

            ResponsePaymentDTO payment = response.getBody();

            log.info("status={}", payment.getStatus());
            log.info("totalAmount={}", payment.getTotalAmount());
            log.info("paidAt={}", payment.getPaidAt());

            return payment;
        } catch (HttpStatusCodeException e) {
            log.error("PortOne status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new IllegalStateException("PortOne 결제 조회 실패", e);
        }
    }

    public void completeSponsorship(String paymentId, String merchantUid) {
        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("paymentId 없음");
        }
        if (merchantUid == null || merchantUid.isBlank()) {
            throw new IllegalArgumentException("merchantUid 없음");
        }

        ResponsePaymentDTO payment = getPayment(paymentId);

        if (!"PAID".equalsIgnoreCase(payment.getStatus())) {
            throw new IllegalStateException("결제 완료 상태 아님: " + payment.getStatus());
        }

        Sponsorship sponsorship = sponsorshipRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> new EntityNotFoundException("후원 없음"));

        if (sponsorship.getPaymentId() == null) {
            sponsorship.setPaymentId(paymentId);
        } else if (!paymentId.equals(sponsorship.getPaymentId())) {
            throw new IllegalStateException("다른 paymentId가 이미 연결되어 있음");
        }

        if (sponsorship.isPaid()) {
            log.info("이미 처리된 후원. paymentId={}, merchantUid={}", paymentId, merchantUid);
            return;
        }

        if (paymentRepository.existsByPaymentId(paymentId)) {
            log.info("이미 저장된 결제. paymentId={}", paymentId);
            return;
        }

        if (payment.getTotalAmount() == null) {
            throw new IllegalStateException("결제 금액 없음");
        }

        if (payment.getTotalAmount().compareTo(sponsorship.getAmount()) != 0) {
            throw new IllegalStateException("금액 불일치");
        }

        sponsorship.complete();

        paymentRepository.save(
                Payment.builder()
                        .paymentId(paymentId)
                        .sponsorship(sponsorship)
                        .paidAt(payment.getPaidAt() == null ? null : payment.getPaidAt())
                        .payMethod(payment.getPayMethod())
                        .pgProvider(payment.getPgProvider())
                        .receiptUrl(payment.getReceiptUrl())
                        .build()
        );

        Wallet wallet = walletRepository.findByOwnerWithLock(
                sponsorship.getTargetType(),
                sponsorship.getTargetId()
        ).orElseGet(() -> {
            Wallet newWallet = Wallet.builder()
                    .ownerType(sponsorship.getTargetType())
                    .ownerId(sponsorship.getTargetId())
                    .totalAmount(BigDecimal.ZERO)
                    .build();

            return walletRepository.save(newWallet);
        });

        wallet.addBalance(sponsorship.getAmount());
    }

	public BigDecimal getsumPaidAmountByTarget(TargetType targetType,Long id) {
		return  sponsorshipRepository.sumPaidAmountByTarget(targetType,id)
                .orElse(BigDecimal.ZERO);
	}

	public long getCountPaidByTarget(TargetType targetType,Long id) {
		return sponsorshipRepository.countPaidByTarget(targetType,id);
	}
	
	public int calculateProgressPercent(BigDecimal total, BigDecimal goal) {
        if (goal == null || goal.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        int percent = total.multiply(BigDecimal.valueOf(100))
                .divide(goal, 0, java.math.RoundingMode.DOWN)
                .intValue();

        return Math.min(percent, 100);
    }

	public String createSponsorship(TargetType targetType,Long targetId,BigDecimal amount,String supporterName,String message,boolean anonymous,Users user) {

			String paymentId = "order_" + System.currentTimeMillis();
			
			supporterName = supporterName.trim();
			message = message.trim();
			
			Sponsorship sponsorship = Sponsorship.builder()
			.merchantUid(paymentId)
			.user(user)
			.targetType(targetType)
			.targetId(targetId)
			.amount(amount)
			.supporterName(supporterName)
			.message(message)
			.anonymous(anonymous)
			.status(SponsorshipStatus.PENDING)
			.build();
			
			sponsorshipRepository.save(sponsorship);
			
			return paymentId;
		}

	public List<ResponseSupportHistoryListDTO> getRecentSupportHistory(TargetType team, Long id, PageRequest of) {
		System.out.println("targetType = " + team);
	    System.out.println("targetId = " + id);
	    System.out.println("pageable = " + of);

	    List<ResponseSupportHistoryListDTO> result =
	            paymentRepository.findRecentSupportHistory(team, id, of);

	    System.out.println("recentSupports result size = " + result.size());

	    return result;
	}

}