package com.creatival.purchase.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.creatival.content.Content;
import com.creatival.like.TargetType;
import com.creatival.purchase.Purchase;
import com.creatival.user.Users;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
	 boolean existsByUserAndTargetTypeAndTargetId(Users user, TargetType targetType, Long targetId);



    List<Purchase> findByUserOrderByPurchasedAtDesc(Users user);

	Optional<Purchase> findByUserAndTargetTypeAndTargetId(Users user, TargetType episode, Long id);
}
