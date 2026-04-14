package com.creatival.sponsorship.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PortOnePaymentSummary {
    private String paymentId;
    private String status;
    private BigDecimal totalAmount;
    private String orderName;
    private String currency;
    private String paidAt;
    private String method;
    private String rawJson;
}