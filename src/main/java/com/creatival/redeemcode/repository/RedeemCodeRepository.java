package com.creatival.redeemcode.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.creatival.like.TargetType;
import com.creatival.redeemcode.RedeemCode;
import com.creatival.user.Users;

public interface RedeemCodeRepository extends JpaRepository<RedeemCode, Long> {

    Optional<RedeemCode> findByCode(String code);

    boolean existsByCode(String code);

    List<RedeemCode> findByIssuerOrderByCreatedAtDesc(Users issuer);

    List<RedeemCode> findByTargetTypeAndTargetIdOrderByCreatedAtDesc(TargetType targetType, Long targetId);
}