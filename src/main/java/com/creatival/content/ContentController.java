package com.creatival.content;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.creatival.content.DTO.CreateNovelDTO;
import com.creatival.content.DTO.ResponseNovelDetail;
import com.creatival.content.DTO.ResponseNovelList;
import com.creatival.content.Enum.OwnerType;
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
	public String novel_list(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
		Page<ResponseNovelList> paging = contentService.getNovelList(page);
		model.addAttribute("paging", paging);
		return "novel_list";
	}
	
	@GetMapping("/novel_write")
	public String novel_write(CreateNovelDTO createNovelDTO) {
		return "novel_write";
	}
	
	@PostMapping("/novel_write")
	public String novel_wrtie(@Valid CreateNovelDTO createNovelDTO, BindingResult bindingResult, Principal principal) {
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
	
	@GetMapping("/novel_detail/{id}")
	public String novel_detail(Model model, @PathVariable("id") Long id, Principal principal) {
		Content content = contentService.getNovel(id);
		ResponseNovelDetail novelDetail = ResponseNovelDetail.from(content);
		model.addAttribute("novel", novelDetail);
		model.addAttribute("loginUsername", principal.getName());
		return "novel_detail";
	}
	
	@PostMapping("/novel_delete/{id}")
	public String novel_delete(@PathVariable Long id, Principal principal) {
		Content content = contentService.getNovel(id);
		
		if(content.getOnwerType()!=OwnerType.TEAM && content.getUser().getUsername() != principal.getName()) {
			contentService.delete(content);
			return "redirect:/content/novel_list";
		}
		
		if(content.getOnwerType()==OwnerType.TEAM) {
			return "redirect:content/novel_detail/"+id+"?error=팀 컨텐츠를 함부로 지울 수는 없습니다.";
		}
		if(content.getUser().getUsername() != principal.getName()) {
			return "redirect:content/novel_detail/"+id+"?error=콘텐츠의 소유자가 아닙니다.";
		}
		return "redirect:content/novel_detail/"+id+"?error=알 수 없는 오류가 발생했습니다.";
	}
}
