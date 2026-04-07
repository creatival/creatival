package com.creatival.user;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.catalina.User;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.creatival.MailService;
import com.creatival.follow.FollowService;
import com.creatival.like.TargetType;
import com.creatival.tag.ResponseTagDTO;
import com.creatival.tag.TagService;
import com.creatival.token.UserToken;
import com.creatival.token.UserTokenService;
import com.creatival.user.DTO.RequestSignUp;
import com.creatival.user.DTO.RequestUpdateUser;
import com.creatival.user.DTO.ResponseProfile;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final MailService mailService;
	private final UserService userService;
	private final UserTokenService userTokenService;
	private final TagService tagService;
	private final FollowService followService;
	
	@GetMapping("/signUp")
	public String signup(Model model) {
		model.addAttribute("signUpRequest", new RequestSignUp());
		return "signup_form";
	}
	
	@PostMapping("/signUp")
	public String signUp(@Valid @ModelAttribute("signUpRequest") RequestSignUp signUpRequest ,BindingResult bindingResult) {
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
	public String myPage(Model model, Principal principal, RedirectAttributes redirectAttributes) {
		if(principal == null) {
			redirectAttributes.addFlashAttribute("isLogMsg", true);
			return "redirect:/";
		}
		Users user = userService.getUserByUsername(principal.getName());
		model.addAttribute("user", ResponseProfile.from(user));
		List<ResponseTagDTO> userTags = tagService.getTagForUser(user);
		model.addAttribute("tagList", userTags);
		return "mypage_home";
	}
	
	@GetMapping("/myPage/{id}")
	public String profilePage(Model model, @PathVariable("id") Long id, Principal principal) {
		Users user = userService.getUserById(id);
		model.addAttribute("user", ResponseProfile.from(user));
		List<ResponseTagDTO> userTags = tagService.getTagForUser(user);
		model.addAttribute("tagList", userTags);
		
		Users loginUser =null;
		if(principal != null) {
			loginUser = userService.getUserByUsername(principal.getName());
		}
		model.addAttribute("follow", followService.getFollow(loginUser, TargetType.USER, user.getId()));
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
			bindingResult.reject("UpdateUserFailed", e.getMessage());
			return "redirect:/user/edit";
		} catch (Exception e) {
			e.printStackTrace();
	        bindingResult.reject("UpdateUserFailed", "정보 수정 중 알 수 없는 오류가 발생했습니다.");
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
	@GetMapping("/activeUser")
	public String activeUser() {
		return "activeUserForm";
	}
	
	@PostMapping("activeUser")
	public String activeUser(@RequestParam("email") String email) throws MessagingException {
		Users user = userService.getUserByEmail(email);
		if(user == null) {
			String msg = URLEncoder.encode("유저를 찾지 못 했습니다.", StandardCharsets.UTF_8);
			return "redirect:/user/activeUser?error=notFoundUser&message="+msg;
		}
		if(!user.isDeleted()) {
			String msg = URLEncoder.encode("비활성화된 유저가 아닙니다.", StandardCharsets.UTF_8);
			return "redirect:/user/activeUser?error=notDisableUser&message="+msg;
		}
		
		String link = userService.createActiveUserMailLink(user);
		
		mailService.sendActiveUser(email, link);
		
		return "sucess_send_mail";
	}
	
	//얘는 메일 인증을 통해 들어오는 녀석임 그래서 루트 페이지로 보냄
	@GetMapping("/active/confirm")
	public String confirm(@RequestParam("token") String token) {
		
		try {
			UserToken userToken = userTokenService.getUserTokenByToken(token);
			Users user = userToken.getUser();
			userService.userActivate(user);
			userTokenService.deleteToken(userToken);
			return "active_success";
		} catch (IllegalStateException e) {
			return "active_fail?error=notFoundToken&message=토큰이 맞지 않습니다.";
		} catch (Exception e) {
			return "active_fail?error=Exception&message=예상치 못 오류가 발견되었습니다.";
		}
	}
	
}
