package com.creatival.sponsorship;

import java.math.BigDecimal;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.creatival.sponsorship.PortOneProperties;
import com.creatival.sponsorship.dto.PortOnePaymentSummary;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PortOnePaymentService {

    private final PortOneAuthService authService;
    private final PortOneProperties properties;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PortOnePaymentSummary getPayment(String paymentId) {
        try {
            String url = properties.getBaseUrl() + "/payments/" + paymentId;
            String accessToken = authService.getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            String rawJson = response.getBody();
            if (rawJson == null || rawJson.isBlank()) {
                throw new IllegalStateException("PortOne 결제 응답이 비어 있습니다.");
            }

            JsonNode root = objectMapper.readTree(rawJson);

            // 응답 구조가 복합적일 수 있으므로 안전하게 탐색
            String status = text(root, "status");
            String orderName = text(root, "orderName");
            String currency = text(root, "currency");
            String paidAt = text(root, "paidAt");
            String method = text(root, "method");

            // totalAmount / amount.total 등 다양한 경우 대비
            BigDecimal totalAmount = decimal(root.path("amount").path("total"));
            if (totalAmount == null) {
                totalAmount = decimal(root.path("totalAmount"));
            }
            if (totalAmount == null) {
                totalAmount = decimal(root.path("amount"));
            }

            return PortOnePaymentSummary.builder()
                    .paymentId(paymentId)
                    .status(status)
                    .orderName(orderName)
                    .currency(currency)
                    .paidAt(paidAt)
                    .method(method)
                    .totalAmount(totalAmount)
                    .rawJson(rawJson)
                    .build();

        } catch (HttpStatusCodeException e) {
            throw new IllegalStateException("PortOne 결제 조회 실패: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new IllegalStateException("PortOne 결제 응답 파싱 실패", e);
        }
    }

    public boolean isPaid(String paymentId) {
        PortOnePaymentSummary payment = getPayment(paymentId);
        return "PAID".equalsIgnoreCase(payment.getStatus())
                || "paid".equalsIgnoreCase(payment.getStatus());
    }

    public void validatePaidAmount(String paymentId, BigDecimal expectedAmount) {
        PortOnePaymentSummary payment = getPayment(paymentId);

        if (!isPaid(paymentId)) {
            throw new IllegalStateException("결제가 완료 상태가 아닙니다. status=" + payment.getStatus());
        }

        if (payment.getTotalAmount() == null) {
            throw new IllegalStateException("결제 금액을 확인할 수 없습니다.");
        }

        if (payment.getTotalAmount().compareTo(expectedAmount) != 0) {
            throw new IllegalStateException(
                    "결제 금액 불일치. expected=" + expectedAmount + ", actual=" + payment.getTotalAmount()
            );
        }
    }

    private String text(JsonNode node, String fieldName) {
        JsonNode value = node.path(fieldName);
        return value.isMissingNode() || value.isNull() ? null : value.asText();
    }

    private BigDecimal decimal(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        try {
            return new BigDecimal(node.asText());
        } catch (Exception e) {
            return null;
        }
    }
}