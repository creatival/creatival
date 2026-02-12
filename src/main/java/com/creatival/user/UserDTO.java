package com.creatival.user;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class UserDTO {
	
	@NoArgsConstructor //파라미터 없는 생성자를 만들며 외부에서 생성 못 하게 함 설명이 필요할 것 같아 넣음 - 김진회
	@Getter
	@Setter
	@AllArgsConstructor
	public static class SignUpRequest {
		
		
		@NotEmpty(message = "아이디는 필수 입력 요소입니다.")
		@Size(max = 50, min = 8, message = "아이디는 8글자 이상으로 작성해주십시오")
		private String username;
		
		@NotEmpty(message = "표시명은 필수 입력 요소입니다.")
		@Size(max = 255, message = "표시명이 너무 깁니다.")
		private String displayName;
		
		@NotEmpty(message = "이메일은 필수 입력 요소입니다.")
		@Email(message = "올바른 이메일을 입력해주십시오")
		private String email;
		
		@NotEmpty(message = "사용할 비밀번호를 입력해주세요.")
		@Size(max = 255, min = 8, message = "비밀번호는 8글자 이상으로 작성하여주십시오")
		private String password;
		
		@NotEmpty(message = "비밀번호 확인을 채워주십시오")
		@Size(max = 255, min = 8, message = "비밀번호는 8글자 이상으로 작성하여주십시오")
		private String password2;
		
		private MultipartFile profileImg;
		
		private String description;
		
		private boolean creator;
		
		public Users toEntity(String encodedPassword, String profileImgUrl) {
			Users users = new Users();
			users.setUsername(username);
			users.setDisplayName(displayName);
			users.setEmail(email);
			users.setPassword(encodedPassword);
			if( profileImgUrl != null &&  !profileImgUrl.isBlank()) {
				users.setProfileImgUrl(profileImgUrl);
			}
			if(description != null && !description.isBlank()) {
				users.setDescription(description);
			}
			users.setCreator(creator);
			
			return users;
		}
	}
	
	@Getter
	@Builder // 새로 써보기로 한 거임
	@AllArgsConstructor
	public static class ProfileResponse {
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
        
        public static ProfileResponse from(Users users) {
        	return ProfileResponse.builder()
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
        			.build(); // 마지막에 무조건 달아줘야 하는거
        			
        }
	}
}
