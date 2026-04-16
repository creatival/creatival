package com.creatival.sponsorship.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebhookDTO {
    private String type;
    private Data data;

    @Getter
    @Setter
    public static class Data {
        private String paymentId;
        private String transactionId;
        private String storeId;
    }
}