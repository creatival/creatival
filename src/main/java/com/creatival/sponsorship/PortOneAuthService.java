package com.creatival.sponsorship;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.creatival.sponsorship.PortOneProperties;
import com.creatival.sponsorship.dto.PortOneTokenResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PortOneAuthService {

    private final PortOneProperties properties;
    private final RestTemplate restTemplate = new RestTemplate();

    private String accessToken;
    private Instant expiresAt;

    public synchronized String getAccessToken() {
        if (accessToken != null && expiresAt != null && Instant.now().isBefore(expiresAt.minusSeconds(30))) {
            return accessToken;
        }
        return issueNewToken();
    }

    private String issueNewToken() {
        String url = properties.getBaseUrl() + "/login/api-secret";

        Map<String, String> body = new HashMap<>();
        body.put("apiSecret", properties.getSecret());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<PortOneTokenResponse> response =
                restTemplate.postForEntity(url, request, PortOneTokenResponse.class);

        PortOneTokenResponse token = response.getBody();
        if (token == null || token.getAccessToken() == null || token.getAccessToken().isBlank()) {
            throw new IllegalStateException("PortOne access token 발급 실패");
        }

        this.accessToken = token.getAccessToken();

        // JWT exp를 직접 파싱하지 않고 일단 짧게 캐시
        this.expiresAt = Instant.now().plusSeconds(25 * 60);

        return this.accessToken;
    }
}