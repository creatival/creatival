package com.creatival.token;

import java.util.Optional;

import org.springframework.stereotype.Service;

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
	
	public void deleteToken(UserToken userToken) {
		if(userToken == null) return;
		
		userTokenRepository.delete(userToken);
	}
}
