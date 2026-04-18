package com.creatival.sponsorship.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseSupportHistoryListDTO {
	private String displayName;
    private BigDecimal amount;
    private String message;
    private boolean anonymous;
    private OffsetDateTime paidAt;

    public ResponseSupportHistoryListDTO(String displayName,
                                         BigDecimal amount,
                                         String message,
                                         boolean anonymous,
                                         OffsetDateTime paidAt) {
        this.displayName = displayName;
        this.amount = amount;
        this.message = message;
        this.anonymous = anonymous;
        this.paidAt = paidAt;
    }
    
    
}
