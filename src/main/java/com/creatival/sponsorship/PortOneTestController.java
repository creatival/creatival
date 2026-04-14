package com.creatival.sponsorship;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.creatival.sponsorship.dto.PortOnePaymentSummary;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PortOneTestController {

    private final PortOnePaymentService portOnePaymentService;

    @GetMapping("/test/portone/payment/{paymentId}")
    public PortOnePaymentSummary getPayment(@PathVariable String paymentId) {
        return portOnePaymentService.getPayment(paymentId);
    }
}