package com.creatival.content;

import java.io.IOException;
import java.security.Principal;

import org.springframework.data.domain.Page;
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

import com.creatival.content.DTO.CreateNovelDTO;
import com.creatival.content.DTO.CreateNovelEpisodeDTO;
import com.creatival.content.DTO.ResponseNovelDetail;
import com.creatival.content.DTO.ResponseNovelEpisodeDetail;
import com.creatival.content.DTO.ResponseNovelEpisodeList;
import com.creatival.content.DTO.ResponseNovelList;
import com.creatival.content.DTO.UpdateNovelDTO;
import com.creatival.content.DTO.UpdateNovelEpisodeDTO;
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
	
	// 수정할 것
	
	@GetMapping("/novel_detail/{id}")
	public String novel_detail(Model model, @PathVariable("id") Long id,@RequestParam(defaultValue = "0", name = "page") int page, Principal principal) {
		Content content = contentService.getNovel(id);
		ResponseNovelDetail novelDetail = ResponseNovelDetail.from(content);
		model.addAttribute("novel", novelDetail);
		if(principal != null) {
			model.addAttribute("loginUsername", principal.getName());
		} else {
			model.addAttribute("loginUsername", null);
		}
		Page<ResponseNovelEpisodeList> paging =  contentService.getEpisodeBySeries(content.getSeries(), page);
		model.addAttribute("paging", paging);
		return "novel_detail";
	}
	
	@GetMapping("/novel_delete/{id}")
	public String novel_delete(@PathVariable("id") Long id, Principal principal) {
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
	@GetMapping("/novel_update/{id}")
	public String novel_update(@PathVariable("id") Long id, Model model) {
		Content novel = contentService.getNovel(id);
		model.addAttribute("novel", UpdateNovelDTO.from(novel, novel.getSeries()));
		model.addAttribute("novelId", id);
		return "novel_edit";
	}
	@PostMapping("/novel_update/{id}")
	public String novel_update(@PathVariable("id") Long id,@Valid @ModelAttribute("novel") UpdateNovelDTO updateNovelDTO, BindingResult bindingResult, Principal principal,Model model)  {
		if(bindingResult.hasErrors()) {
			System.out.println("오류 발생");
			return "novel_edit";
		}
		
		try {
			contentService.updateContentNovel(updateNovelDTO, id, principal.getName());
			return "redirect:/content/novel_detail/"+id;
		} catch (IllegalArgumentException e) {
			bindingResult.reject("updateNovelFailed", e.getMessage());
			return "novel_edit";
		} catch (Exception e) {
			System.out.println(e.getMessage());
			bindingResult.reject("updateNovelFailed", "소설을 수정하던 중 알 수 없는 오류가 발생했습니다.");
	        return "novel_edit";
		}
	}
	
	@PostMapping("/updateNovelThumbnail/{id}")
	public String updateNovelThumbnail(@PathVariable("id") Long id, @RequestParam("thumbnailFile") MultipartFile img, Principal principal) throws IOException {
		contentService.updateNovelThumbnail(id, img, principal.getName());
		return "redirect:/content/novel_detail/" + id;
	}
	
	@GetMapping("/novel/episode/write/{id}")
	public String createNovelEpisode(CreateNovelEpisodeDTO createNovelEpisodeDTO ,@PathVariable("id") Long id, Model model) {
		Content content = contentService.getNovel(id);
		model.addAttribute("novel", ResponseNovelDetail.from(content));
		return "novel_episode_write";
	}
	
	@PostMapping("/novel/episode/write/{id}")
	public String createNovelEpisode(@PathVariable("id") Long id, @Valid CreateNovelEpisodeDTO createNovelEpisodeDTO, BindingResult bindingResult, Principal principal, Model model) {
		if(bindingResult.hasErrors()) {
			System.out.println("오류 발생");
			return "novel_episode_write";
		}
		
		Content content = contentService.getNovel(id);
		model.addAttribute("novel", ResponseNovelDetail.from(content));
		
		try {
			contentService.createNovelEpisode(id, createNovelEpisodeDTO, principal.getName());
			return "redirect:/content/novel_detail/" + id;
		} catch (Exception e) {
			bindingResult.reject("createEpisodeFailed", e.getMessage());
			return "novel_episode_write";
		}
	}
	
	//나중에 권한 체크 넣을 것
	@GetMapping("/novel/episode/{id}")
	public String novelEpisodeDetail(@PathVariable("id") Long episodeId, Model model) {
		Episode episode = contentService.getNovelEpisode(episodeId); 
		ResponseNovelEpisodeDetail episodeDetail = ResponseNovelEpisodeDetail.from(episode);
		model.addAttribute("episode", episodeDetail);
		
		
		model.addAttribute("prevEpisode", contentService.getPrevEpisode(episode));
		model.addAttribute("nextEpisode", contentService.getNextEpisode(episode));
		model.addAttribute("contentId", episode.getSeries().getContent().getId());
		return "novel_viewer";
	}
	
	@GetMapping("/novel/episode/update/{id}")
	public String novelEpisodeUpdate(@PathVariable("id") Long episodeId, Model model) {
		
		Episode episode = contentService.getNovelEpisode(episodeId);
		Content content = episode.getSeries().getContent();
		model.addAttribute("episode", UpdateNovelEpisodeDTO.from(episode));
		model.addAttribute("novel", ResponseNovelDetail.from(content));
		return "novel_episode_write_edit";
	}
	
	@PostMapping("/novel/episode/update/{id}")
	public String novelEpisodeUpdate(@Valid @ModelAttribute("novel") UpdateNovelEpisodeDTO updateNovelEpisodeDTO,BindingResult bindingResult, Model model, Principal principal) {
		if(bindingResult.hasErrors()) {
			System.out.println("오류 발생");
			return "novel_episode_write";
		}
		try {
			contentService.updateContentNovelEpisode(updateNovelEpisodeDTO, principal.getName());
			return "redirect:/content/novel/episode/"+updateNovelEpisodeDTO.getId();
		}  catch (IllegalArgumentException e) {
			bindingResult.reject("updateNovelFailed", e.getMessage());
			return "novel_episode_write_edit";
		} catch (Exception e) {
			System.out.println(e.getMessage());
			bindingResult.reject("updateNovelEpisodeFailed", "에피소드를 수정하던 중 알 수 없는 오류가 발생했습니다.");
	        return "novel_episode_write_edit";
		}
	}
	@GetMapping("/novel/episode/delete/{id}")
	public String novelEpisodeDelete(@PathVariable("id") Long episodeId, Principal principal) {
		Episode episode = contentService.getNovelEpisode(episodeId);
		if(!episode.getSeries().getContent().getUser().getUsername().equals(principal.getName())) {
			return "/";
		}
		contentService.deleteNovelEpisode(episode);
		return "redirect:/content/novel_detail/"+episode.getSeries().getContent().getId();
	}
}
