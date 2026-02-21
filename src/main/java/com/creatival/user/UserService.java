package com.creatival.user;

import java.io.IOException;
import java.net.Authenticator.RequestorType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.creatival.user.DTO.RequestSignUp;
import com.creatival.user.DTO.RequestUpdateUser;
import com.creatival.user.DTO.ResponseProfile;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	private final  UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private static final String UPLOAD_DIR = "src/main/resources/static/images/user";
	
	//String username, String email, String password, String displayname,String description, boolean isCreator
	
	public Users getUserByUsername(String username) {
		Optional<Users> user = userRepository.findByUsername(username);
		if(user.isPresent()) {
			return user.get();
		}
		return null;
	}
	public Users getUserByEmail(String email) {
		Optional<Users> user = userRepository.findByEmail(email);
		if(user.isPresent()) {
			return user.get();
		}
		return null;
	}
	
	public ResponseProfile create(RequestSignUp signUpRequest) throws IOException {
		
		System.out.println(signUpRequest.getUsername());
		if(userRepository.findByUsername(signUpRequest.getUsername()).isPresent()) {
			throw new IllegalStateException("이미 동일한 아이디가 존재합니다.");
		}
		if(userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
			throw new IllegalStateException("이미 동일한 이메일이 존재합니다.");
		}
		if(!signUpRequest.getPassword().equals(signUpRequest.getPassword2())) {
			throw new IllegalStateException("비밀번호가 같지 않습니다.");
		}
		
		String profileImgUrl = null;
		
		if(signUpRequest.getProfileImg()!=null) {
			String fileName = UUID.randomUUID().toString()+"_"+ signUpRequest.getProfileImg().getOriginalFilename();
			Path filePath = Paths.get(UPLOAD_DIR, fileName);
			Files.createDirectories(filePath.getParent());
			Files.write(filePath, signUpRequest.getProfileImg().getBytes());
			profileImgUrl = "/images/user/"+fileName;
		}
		
		
		
		Users user = new Users(
				signUpRequest.getUsername(),
				signUpRequest.getDisplayName(),
				signUpRequest.getEmail(),
				passwordEncoder.encode(signUpRequest.getPassword()),
				signUpRequest.isCreator()
		);
		user.setProfileImgUrl(profileImgUrl);
		user.setDescription(signUpRequest.getDescription());

		
		userRepository.save(user);
		System.out.println("저장됨");
		return ResponseProfile.from(user);
	}
	
	public ResponseProfile edit(RequestUpdateUser profile, Users user) {
		if(!profile.getEmail().equals(user.getEmail())) {
			if(userRepository.findByEmail(profile.getEmail()).isPresent()) {
				throw new IllegalStateException("이미 동일한 이메일이 존재합니다.");
			}
		}
		//이메일 인증 시스템 도입 예정
		user.setEmail(profile.getEmail());
		user.setDescription(profile.getDescription());
		user.setDisplayName(profile.getDisplayName());
		
		userRepository.save(user);
		return ResponseProfile.from(user);
	}
	
	public void userDisable(Users user) {
		user.setDeleted(true);
		user.setDeletedAt(LocalDateTime.now());
		userRepository.save(user);
	}
	
	public void updateProfileImg(Users user, MultipartFile file) throws IOException {
		String profileImgUrl=null;
		String fileName = UUID.randomUUID().toString()+"_"+ file.getOriginalFilename();
		Path filePath = Paths.get(UPLOAD_DIR, fileName);
		Files.createDirectories(filePath.getParent());
		Files.write(filePath, file.getBytes());
		profileImgUrl = "/images/user/"+fileName;
		
		user.setProfileImgUrl(profileImgUrl);
		
		userRepository.save(user);
	}
}
