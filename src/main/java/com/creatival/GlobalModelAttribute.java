package com.creatival;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.creatival.user.UserService;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@ControllerAdvice
public class GlobalModelAttribute {
	private final UserService userService;
	
    @ModelAttribute
    @Cacheable(value = "profileImage", key = "#username")
    public void addProfileImage(Model model, Authentication authentication) {
    	
    	

        String defaultImg = "https://dummyimage.com/32x32/ced4da/6c757d";

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {

            model.addAttribute("profileImageUrl", defaultImg);
            return;
        }

        // 여기서만 DB 조회 (또는 CustomUser면 캐스팅)
        String username = authentication.getName();

        String profileImageUrl = userService.getProfileImageUrl(username);

        model.addAttribute("profileImageUrl",
                profileImageUrl != null ? profileImageUrl : defaultImg);
    }
}