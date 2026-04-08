package com.creatival.token;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.creatival.user.Users;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserTokenService {
	private final UserTokenRepository userTokenRepository;
	
	public UserToken getUserTokenByToken(String token) {
		Optional<UserToken> userToken = userTokenRepository.findByToken(token);
		if(userToken.isPresent()) {
			return userToken.get();
		}
		return null;
	}
	public UserToken getUserTokenById(Long tokenId) {
		Optional<UserToken> userToken = userTokenRepository.findById(tokenId);
		if(userToken.isPresent()) {
			return userToken.get();
		}
		return null;
	}
	public UserToken createUserToken(Users user) {
		Optional<UserToken> optional =userTokenRepository.findByUser(user);
		if(optional.isPresent()) {
			return optional.get();
		}
		UserToken token = UserToken.create(user);
		userTokenRepository.save(token);
		
		return token;
	}
	
	public void deleteToken(UserToken userToken) {
		if(userToken == null) return;
		
		userTokenRepository.delete(userToken);
	}
}
