package com.creatival.sponsorship.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortOneTokenResponse {
    private String accessToken;
    private String refreshToken;
}