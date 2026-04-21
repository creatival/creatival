package com.creatival.redeemcode.dto;

import java.time.LocalDateTime;

import com.creatival.like.TargetType;
import com.creatival.redeemcode.RedeemCode;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseRedeemCodeDTO {
    private Long id;
    private String code;
    private TargetType targetType;
    private Long targetId;
    private boolean used;
    private String usedByUsername;
    private LocalDateTime usedAt;
    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;

    public static ResponseRedeemCodeDTO from(RedeemCode entity) {
        return ResponseRedeemCodeDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .targetType(entity.getTargetType())
                .targetId(entity.getTargetId())
                .used(entity.isUsed())
                .usedByUsername(entity.getUsedBy() != null ? entity.getUsedBy().getUsername() : null)
                .usedAt(entity.getUsedAt())
                .expiredAt(entity.getExpiredAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}