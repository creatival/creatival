package com.creatival.user;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
	private final UserService userService;
	
	@GetMapping("/signUp")
	public String signup(UserDTO.SignUpRequest signUpRequest) {
		return "signup_form";
	}
	
	@PostMapping("/signUp")
	public String signUp(@Valid UserDTO.SignUpRequest signUpRequest ,BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			System.out.println("오류 발생");
			return "signup_form";
		}
			System.out.println("정상 진행");
		
			
			try {
		        // 저장
		        userService.create(signUpRequest); 
		        return "redirect:/";
		        
		    } catch (IllegalStateException e) { // 이미 존재할 경우 서비스에서 넣은 오류가 잡힘
		        bindingResult.reject("signupFailed", e.getMessage());
		        return "signup_form";
		    } catch (Exception e) {
		        e.printStackTrace();
		        bindingResult.reject("signupFailed", "회원가입 중 알 수 없는 오류가 발생했습니다.");
		        return "signup_form";
		    }
	}
	
	@GetMapping("/login")
	public String login_form() {
		return "login_form";
	}
	
	@GetMapping("/myPage")
	public String myPage(Model model, Principal principal) {
		Users user = userService.getUserByUsername(principal.getName());
		
		model.addAttribute("user", UserDTO.ProfileResponse.from(user));
		return "mypage_home";
	}
	
}
