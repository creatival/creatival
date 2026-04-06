package com.creatival.bookmark.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.creatival.bookmark.Bookmark;
import com.creatival.like.TargetType;
import com.creatival.user.Users;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
	Optional<Bookmark> findByUserAndTargetTypeAndTargetId(Users user, TargetType targetType, Long targetId);
	
	long countByTargetIdAndTargetType(Long targetId, TargetType targetType);
}
