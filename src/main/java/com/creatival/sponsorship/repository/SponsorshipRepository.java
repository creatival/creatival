package com.creatival.sponsorship.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.creatival.like.TargetType;
import com.creatival.sponsorship.Sponsorship;
import com.creatival.sponsorship.Wallet;
import com.creatival.user.Users;


public interface SponsorshipRepository extends JpaRepository<Sponsorship, Long> {

	List<Sponsorship> findByUser(Users user);

	Optional<Sponsorship> findByMerchantUid(String merchantUid);
	Optional<Sponsorship> findByPaymentId(String paymentId);
	
	@Query("""
	        select coalesce(sum(s.amount), 0)
	        from Sponsorship s
	        where s.targetType = :targetType
	          and s.targetId = :targetId
	          and s.status = com.creatival.sponsorship.SponsorshipStatus.PAID
	    """)
    Optional<BigDecimal> sumPaidAmountByTarget(@Param("targetType") TargetType targetType,@Param("targetId") Long targetId);

	@Query("""
		    select count(s)
		    from Sponsorship s
		    where s.targetType = :targetType
		      and s.targetId = :targetId
		      and s.status = com.creatival.sponsorship.SponsorshipStatus.PAID
		""")
		long countPaidByTarget(
		    @Param("targetType") TargetType targetType, 
		    @Param("targetId") Long targetId
		);
    
    

}
