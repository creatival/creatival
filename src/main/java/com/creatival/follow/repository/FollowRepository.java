package com.creatival.follow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.creatival.follow.Follow;
import com.creatival.like.Likes;
import com.creatival.like.TargetType;
import com.creatival.user.Users;

public interface FollowRepository extends JpaRepository<Follow, Long> {
	Optional<Follow> findByUserAndTargetTypeAndTargetId(Users user, TargetType targetType, Long targetId);

	Long countByTargetIdAndTargetType(Long targetId, TargetType type);
}
