package com.creatival.sponsorship.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.creatival.sponsorship.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	boolean existsByPaymentId(String paymentId);

}
