package com.creatival.token;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.creatival.user.Users;



@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
	Optional<UserToken> findByToken(String token);
	Optional<UserToken> findByUser(Users user);
}
