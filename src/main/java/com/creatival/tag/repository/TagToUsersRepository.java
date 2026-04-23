package com.creatival.tag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import com.creatival.user.Users;

import jakarta.transaction.Transactional;

import com.creatival.tag.Tag;
import com.creatival.tag.TagToUsers;



public interface TagToUsersRepository extends JpaRepository<TagToUsers, Long> {
	List<TagToUsers> findByUser(Users user);
	
	@Modifying
    @Transactional
    @Query("DELETE FROM TagToUsers tm WHERE tm.user.id = :userId AND tm.tag.id = :tagId")
    void deleteByUserIdAndTagId(@Param("userId") Long userId, @Param("tagId") Long tagId);
	
	void deleteByUser(Users user);
	
	@Query("""
		    select distinct u
		    from TagToUsers ttu
		    join ttu.tag t
		    join ttu.user u
		    where lower(t.tagText) = lower(:keyword)
		    order by u.id desc
		""")
		List<Users> searchUsersByTag(@Param("keyword") String keyword);
}
