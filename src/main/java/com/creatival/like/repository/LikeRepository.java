package com.creatival.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.creatival.like.Likes;
import java.util.List;
import java.util.Optional;

import com.creatival.user.Users;
import com.creatival.like.TargetType;



public interface LikeRepository extends JpaRepository<Likes, Long>{
	Optional<Likes> findByUserAndTargetTypeAndTargetId(Users user, TargetType targetType, Long targetId);
	
	long countByTargetIdAndTargetType(Long targetId, TargetType targetType);
}
