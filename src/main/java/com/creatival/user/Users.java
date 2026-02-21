package com.creatival.user;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class Users {
	
	protected Users() {
		
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true, length =  50, nullable = false)
	private String username;
	
	@Column(length = 50, nullable = false)
	private String displayName;
	
	@Column(length = 320, nullable = false, unique = true)
	private String email;
	
	@Column(nullable = false, length = 255)
	private String password;
	
	@Column(length = 500)
	private String profileImgUrl;
	
	@Column(columnDefinition = "TEXT")
	private String description;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private userRole role = userRole.USER;
	
	@Column(nullable = false)
	private boolean isCreator = false;
	
	@Column(nullable = false)
	private boolean isVerification = false;
	
	@CreationTimestamp
	@Column(nullable = false)
	private LocalDateTime createdAt;
	
	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
	private boolean isDeleted = false;
	
	private LocalDateTime deletedAt;

	public Users(String username, String displayName, String email, String password, boolean isCreator) {
		super();
		this.username = username;
		this.displayName = displayName;
		this.email = email;
		this.password = password;
		this.isCreator = isCreator;
	}
	
	
}
