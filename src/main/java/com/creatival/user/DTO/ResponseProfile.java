package com.creatival.user.DTO;

import java.time.LocalDateTime;

import com.creatival.user.Users;
import com.creatival.user.userRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder // 새로 써보기로 한 거임
@AllArgsConstructor
public class ResponseProfile {
	private Long id;
    private String username;
    private String email;
    private String displayName;
    private String profileImgUrl;
    private String description;
    private userRole role;
    private boolean isCreator;
    private boolean isVerification;
    private LocalDateTime createdAt;
    private boolean supportEnabled;
    
    public static ResponseProfile from(Users users) {
    	if (users == null) return null;
    	return ResponseProfile.builder()
    			.id(users.getId())
    			.username(users.getUsername())
    			.email(users.getEmail())
    			.displayName(users.getDisplayName())
    			.profileImgUrl(users.getProfileImgUrl())
    			.description(users.getDescription())
    			.role(users.getRole())
    			.isCreator(users.isCreator())
    			.isVerification(users.isVerification())
    			.createdAt(users.getCreatedAt())
    			.supportEnabled(users.isSupportEnabled())
    			.build(); // 마지막에 무조건 달아줘야 하는거
    			
    }
}
