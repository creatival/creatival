package com.creatival.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import com.creatival.user.Users;

import jakarta.transaction.Transactional;

import com.creatival.tag.Tag;



public interface TagToUsersRepository extends JpaRepository<TagToUsers, Long> {
	List<TagToUsers> findByUser(Users user);
	
	@Modifying
    @Transactional
    @Query("DELETE FROM TagToUsers tm WHERE tm.user.id = :userId AND tm.tag.id = :tagId")
    void deleteByUserIdAndTagId(@Param("userId") Long userId, @Param("tagId") Long tagId);
	
	void deleteByUser(Users user);
}
