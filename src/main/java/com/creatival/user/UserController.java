package com.creatival.user;

import java.io.IOException;
import java.security.Principal;

import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.creatival.user.DTO.RequestSignUp;
import com.creatival.user.DTO.RequestUpdateUser;
import com.creatival.user.DTO.ResponseProfile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
	private final UserService userService;
	
	@GetMapping("/signUp")
	public String signup(@ModelAttribute("signUpRequest") RequestSignUp signUpRequest) {
		return "signup_form";
	}
	
	@PostMapping("/signUp")
	public String signUp(@Valid RequestSignUp signUpRequest ,BindingResult bindingResult) {
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
		model.addAttribute("user", ResponseProfile.from(user));
		return "mypage_home";
	}
	
	@GetMapping("/edit")
	public String edit(Model model, Principal principal) {
		Users user = userService.getUserByUsername(principal.getName());
		model.addAttribute("profile", RequestUpdateUser.from(user));
		return "user_edit";
	}
	
	@PostMapping("/edit")
	public String edit(Model model, @Valid @ModelAttribute("profile") RequestUpdateUser profile, BindingResult bindingResult, Principal principal) {
		Users user = userService.getUserByUsername(principal.getName());
		if(bindingResult.hasErrors()) {
			System.out.println("오류 발생");
			return "user_edit";
		}
		try {
			ResponseProfile responseProfile = userService.edit(profile, user);
			model.addAttribute("user", responseProfile);
			return "redirect:/user/myPage";
		} catch (IllegalStateException e) {
			bindingResult.reject("signupFailed", e.getMessage());
			return "redirect:/user/edit";
		} catch (Exception e) {
			e.printStackTrace();
	        bindingResult.reject("signupFailed", "회원가입 중 알 수 없는 오류가 발생했습니다.");
	        return "redirect:/user/edit";
		}
	}
	@GetMapping("/delete")
	public String delete() {
		return "user_delete";
	}
	
	@PostMapping("/delete")
	public String delete(HttpServletRequest request, HttpServletResponse response, Principal principal) {
		Users user = userService.getUserByUsername(principal.getName());
		userService.userDisable(user);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null) {
	        new SecurityContextLogoutHandler().logout(request, response, auth);
	    }

	    // 3. 로그아웃 후 메인으로 리다이렉트
	    return "redirect:/?logout";
	}
	
	@PostMapping("/updateProfileImg")
	public String updateProfileImg(@RequestParam("profileImg") MultipartFile profileImg,Principal principal) throws IOException {
		Users user = userService.getUserByUsername(principal.getName());
		userService.updateProfileImg(user, profileImg);
		return "redirect:/user/myPage";
	}
	
}
