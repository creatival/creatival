package com.creatival.sponsorship.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.creatival.sponsorship.Sponsorship;
import com.creatival.sponsorship.Wallet;

public interface SponsorshipRepository extends JpaRepository<Sponsorship, Long> {

	Optional<Sponsorship> findByMerchantUid(String merchantUid);
	Optional<Sponsorship> findByPaymentId(String paymentId);

}
