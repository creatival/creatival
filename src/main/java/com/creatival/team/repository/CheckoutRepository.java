package com.creatival.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.creatival.team.Checkout;

public interface CheckoutRepository extends JpaRepository<Checkout, Long> {
	@Query("SELECT COALESCE(MAX(c.sortOrder),0) FROM Checkout c WHERE c.project.id = :projectId")
	int findMaxSortOrder(@Param("projectId") Long projectId);
}
