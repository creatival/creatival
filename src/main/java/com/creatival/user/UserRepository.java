package com.creatival.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;



@Repository
public interface UserRepository extends JpaRepository<Users, Long>{
	Optional<Users> findByUsername(String username);
	Optional<Users> findByEmail(String email);
	List<Users> findAllByIsDeleted(boolean deleted);
	List<Users> findByIsDeletedTrueAndDeletedAtBefore(LocalDateTime time);
	
	@Query("""
		    select distinct u
		    from Users u
		    where lower(u.username) like lower(concat('%', :keyword, '%'))
		       or lower(u.displayName) like lower(concat('%', :keyword, '%'))
		    order by u.id desc
		""")
		List<Users> searchUsers(@Param("keyword") String keyword);
}
