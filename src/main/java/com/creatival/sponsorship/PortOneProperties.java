package com.creatival.sponsorship;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "portone.api")
public class PortOneProperties {
    private String baseUrl;
    private String secret;
}