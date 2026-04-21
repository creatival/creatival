package com.creatival.purchase.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseCompleteRequestDTO {
    private String paymentId;
    private String targetType;
    private Long targetId;
}