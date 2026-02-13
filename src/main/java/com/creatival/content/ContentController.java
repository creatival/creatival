package com.creatival.content;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.creatival.user.UserService;
import com.creatival.user.Users;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/content")
public class ContentController {
	private final ContentService contentService;
	private final UserService userService;
	
	@GetMapping("/novel_list")
	public String novel_list() {
		return "novel_list";
	}
	
	@GetMapping("/novel_write")
	public String novel_write(ContentCreateDTO.createNovelDTO createNovelDTO) {
		return "novel_write";
	}
	
	@PostMapping("/novel_write")
	public String novel_wrtie(@Valid ContentCreateDTO.createNovelDTO createNovelDTO, BindingResult bindingResult, Principal principal) {
		if(bindingResult.hasErrors()) {
			System.out.println("오류 발생");
			return "novel_write";
		}
		
		try {
			contentService.createCotentNovel(createNovelDTO, userService.getUserByUsername(principal.getName()));
			return "redirect:/content/novel_list";
		} catch (IllegalStateException e) {
			bindingResult.reject("signupFailed", e.getMessage());
	        return "novel_write";
		} catch (Exception e) {
	        e.printStackTrace();
	        bindingResult.reject("createNovelFailed", "소설을 만들던 중 오류가 발생했습니다.");
	        return "novel_write";
	    }
		
		
	}
}
