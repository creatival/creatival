package com.creatival.token;

import java.util.UUID;
import com.creatival.user.Users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@AllArgsConstructor
public class UserToken {
	
	
	
	protected UserToken() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	//UUID로 이루워진 값
	@Column(nullable = false, unique = true)
	private String token;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Users user;
	
	public static UserToken create(Users user) {
		UserToken userToken = new UserToken();
		userToken.user = user;
		userToken.token = UUID.randomUUID().toString();
		return userToken;
	}
}
