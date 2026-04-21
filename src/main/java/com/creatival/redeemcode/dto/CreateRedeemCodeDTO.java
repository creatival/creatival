package com.creatival.redeemcode.dto;

import java.time.LocalDateTime;

import com.creatival.like.TargetType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRedeemCodeDTO {
    private TargetType targetType;
    private Long targetId;
    private LocalDateTime expiredAt;
}
