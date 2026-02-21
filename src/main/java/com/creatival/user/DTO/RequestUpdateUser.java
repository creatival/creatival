package com.creatival.user.DTO;


import com.creatival.user.Users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@Builder // 새로 써보기로 한 거임
@AllArgsConstructor
public class RequestUpdateUser {
	
	@NotEmpty(message = "표시명은 필수 입력 요소입니다.")
	@Size(max = 255, message = "표시명이 너무 깁니다.")
	private String displayName;
	
	@NotEmpty(message = "이메일은 필수 입력 요소입니다.")
	@Email(message = "올바른 이메일을 입력해주십시오")
	private String email;
	
	private String description;
	
	private boolean creator;
	
	public static RequestUpdateUser from(Users user) {
		return RequestUpdateUser.builder()
				.displayName(user.getDisplayName())
				.email(user.getEmail())
				.description(user.getDescription())
				.creator(user.isCreator())
				.build();
	}
}
