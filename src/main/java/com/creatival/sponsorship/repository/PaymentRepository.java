package com.creatival.sponsorship.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.creatival.like.TargetType;
import com.creatival.sponsorship.Payment;
import com.creatival.sponsorship.dto.ResponseSupportHistoryListDTO;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	boolean existsByPaymentId(String paymentId);
	
	@Query("""
		    select new com.creatival.sponsorship.dto.ResponseSupportHistoryListDTO(
		        case
		            when s.anonymous = true then '익명 사용자'
		            when s.supporterName is not null and trim(s.supporterName) <> '' then s.supporterName
		            when u is not null and u.displayName is not null and trim(u.displayName) <> '' then u.displayName
		            else '익명 사용자'
		        end,
		        s.amount,
		        s.message,
		        s.anonymous,
		        p.paidAt
		    )
		    from Payment p
		    join p.sponsorship s
		    left join s.user u
		    where s.targetType = :targetType
		      and s.targetId = :targetId
		    order by p.paidAt desc
		""")
		List<ResponseSupportHistoryListDTO> findRecentSupportHistory(@Param("targetType") TargetType targetType,
		                                                             @Param("targetId") Long targetId,
		                                                             Pageable pageable);
}
