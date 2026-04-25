package com.creatival.user;

import java.io.IOException;
import java.net.Authenticator.RequestorType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.creatival.FileUtil;
import com.creatival.content.ContentController;
import com.creatival.tag.Tag;
import com.creatival.tag.TagService;
import com.creatival.token.UserToken;
import com.creatival.token.UserTokenRepository;
import com.creatival.token.UserTokenService;
import com.creatival.user.DTO.RequestSignUp;
import com.creatival.user.DTO.RequestUpdateUser;
import com.creatival.user.DTO.ResponseProfile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

    private final String imgPath="/upload/images/user/";
    private final FileUtil fileUtil;
	private final  UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserTokenService userTokenService;




	
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
		Users user = new Users(
				signUpRequest.getUsername(),
				signUpRequest.getDisplayName(),
				signUpRequest.getEmail(),
				passwordEncoder.encode(signUpRequest.getPassword()),
				signUpRequest.isCreator()
		);
		String profileImgUrl = imgPath;
		
		if(signUpRequest.getProfileImg()!=null && !signUpRequest.getProfileImg().isEmpty()) {
			profileImgUrl += fileUtil.saveImage(signUpRequest.getProfileImg(), "user");
			user.setProfileImgUrl(profileImgUrl);
		}
		
		
		
		
		
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
	
	public void userActivate(Users user) {
		user.setDeleted(false);
		user.setDeletedAt(null);
		userRepository.save(user);
	}
	@CacheEvict(value = "profileImage", key = "#username")
	public void updateProfileImg(Users user, MultipartFile file) throws IOException {
		String profileImgUrl=null;
		profileImgUrl = imgPath+fileUtil.saveImage(file, "user");
		
		user.setProfileImgUrl(profileImgUrl);
		
		userRepository.save(user);
	}
	
	public String createActiveUserMailLink(Users user) {
		UserToken token = userTokenService.createUserToken(user);
		
		return "http://localhost:8080/user/active/confirm?token=" + token.getToken();
	}
	
	@Transactional
	public void userDelete(Users user) {
		userRepository.delete(user);
	}
	
	public void userAllDelete(List<Users> users) {
		for(Users user : users) {
			System.out.println("삭제됨, 삭제 대상 : " + user.getUsername());
			user.setUsername("deleted_"+UUID.randomUUID());
			user.setEmail("deleted_"+UUID.randomUUID());
			user.setDisplayName("deleted_"+UUID.randomUUID());
			user.setPassword("deleted_"+UUID.randomUUID());
			user.setProfileImgUrl("deleted_"+UUID.randomUUID());
			
			userRepository.save(user);
		}
	}
	
	public List<Users> getAllByIsDeleted(LocalDateTime threshold) {
		List<Users> list = userRepository.findByIsDeletedTrueAndDeletedAtBefore(threshold);
		return list;
	}
	public Users getUserById(Long userId) {
		return userRepository.findById(userId).get();
	}
	@Transactional
	public void updateSupportSetting(String username, boolean supportEnabled) {
	    Users user = userRepository.findByUsername(username)
	            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

	    user.setSupportEnabled(supportEnabled);
	}
	public String getProfileImageUrl(String username) {
		Users user = getUserByUsername(username);
		if(user==null) {
			return null;
		}
		return user.getProfileImgUrl();
	}
}
