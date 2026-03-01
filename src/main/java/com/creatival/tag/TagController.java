package com.creatival.tag;

import java.security.Principal;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.creatival.content.ContentFileService;
import com.creatival.content.ContentService;
import com.creatival.user.UserService;
import com.creatival.user.Users;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/tag")
public class TagController {
	private final TagService tagService;
	private final UserService userService;
	
	@PostMapping("/createUserTag")
	public String createTagForUser(@RequestParam("tagName") String tagName, Principal principal) {
		Users user = userService.getUserByUsername(principal.getName());
		tagService.createTagForUser(user, tagName);
		return "redirect:/user/myPage";
	}
	@GetMapping("/deleteUserTag")
	public String deleteUserTag(@RequestParam("tagId") Long tagId, Principal principal) {
	    
	    Users user = userService.getUserByUsername(principal.getName());
	    Long userId = user.getId();
	    
	    // 서비스에 삭제 로직 위임
	    tagService.deleteMapping(userId, tagId);
	    
	    return "redirect:/user/myPage"; // 삭제 후 다시 마이페이지로
	}
}
