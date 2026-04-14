package com.creatival.sponsorship.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponsePaymentDTO {

    private String id;
    private String status;
    private String orderName;
    private String currency;
    private OffsetDateTime paidAt;
    private String receiptUrl;

    private Amount amount;
    private Method method;
    private Channel channel;

    public BigDecimal getTotalAmount() {
        return amount == null ? null : amount.getTotal();
    }

    public String getPayMethod() {
        if (method == null) return null;
        if (method.getProvider() != null) return method.getProvider();
        return method.getType();
    }

    public String getPgProvider() {
        return channel == null ? null : channel.getPgProvider();
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Amount {
        private BigDecimal total;
        private BigDecimal taxFree;
        private BigDecimal vat;
        private BigDecimal supply;
        private BigDecimal discount;
        private BigDecimal paid;
        private BigDecimal cancelled;
        private BigDecimal cancelledTaxFree;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Method {
        private String type;
        private String provider;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Channel {
        private String pgProvider;
    }
}