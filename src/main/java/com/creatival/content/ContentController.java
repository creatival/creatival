package com.creatival.content;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

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
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.creatival.content.DTO.CreateArtDTO;
import com.creatival.content.DTO.CreateMusicDTO;
import com.creatival.content.DTO.CreateNovelDTO;
import com.creatival.content.DTO.CreateNovelEpisodeDTO;
import com.creatival.content.DTO.CreateVideoDTO;
import com.creatival.content.DTO.ResponseArtDetail;
import com.creatival.content.DTO.ResponseArtList;
import com.creatival.content.DTO.ResponseMusicDetailDTO;
import com.creatival.content.DTO.ResponseMusicListDTO;
import com.creatival.content.DTO.ResponseNovelDetail;
import com.creatival.content.DTO.ResponseNovelEpisodeDetail;
import com.creatival.content.DTO.ResponseNovelEpisodeList;
import com.creatival.content.DTO.ResponseNovelList;
import com.creatival.content.DTO.ResponseVideoDetailDTO;
import com.creatival.content.DTO.ResponseVideoListDTO;
import com.creatival.content.DTO.UpdateArtDTO;
import com.creatival.content.DTO.UpdateMusicDTO;
import com.creatival.content.DTO.UpdateNovelDTO;
import com.creatival.content.DTO.UpdateNovelEpisodeDTO;
import com.creatival.content.DTO.UpdateVideoDTO;
import com.creatival.content.Enum.OwnerType;
import com.creatival.content.repository.EpisodeRepository;
import com.creatival.tag.ResponseTagDTO;
import com.creatival.tag.TagService;
import com.creatival.user.UserService;
import com.creatival.user.Users;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/content")
public class ContentController {

	private final ContentService contentService;
	private final UserService userService;
	private final ContentFileService contentFileService;
	private final TagService tagService;

	
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
		if (createNovelDTO.getOriginalContentId()!=null && createNovelDTO.getProjectTag() !=null) {
			bindingResult.reject("createArtFailed", "프로젝트에 속하거나 원본 content에 속하거나 하나만 할 수 있습니다");
			return "illustration_write";
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
		
		Long firstEpisodeId = contentService.getFirstEpisodeId(content);
	    model.addAttribute("firstEpisodeId", firstEpisodeId);
		
		List<ResponseTagDTO> contentTags = tagService.getTagForContent(content);
		model.addAttribute("tagList", contentTags);
		return "novel_detail";
	}
	
	@GetMapping("/novel_delete/{id}")
	public String novel_delete(@PathVariable("id") Long id, Principal principal) {
		Content content = contentService.getNovel(id);
		
		if(content.getOwnerType()!=OwnerType.TEAM && content.getUser().getUsername() != principal.getName()) {
			contentService.delete(content);
			return "redirect:/content/novel_list";
		}
		
		if(content.getOwnerType()==OwnerType.TEAM) {
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
	
	
	
	//
	
	@GetMapping("/art/list")
	public String artList(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
		Page<ResponseArtList> paging = contentService.getArtList(page);
		model.addAttribute("paging", paging);
		return "illustration_list";
	}
	
	@GetMapping("/art/create")
	public String createArt(CreateArtDTO createArtDTO) {
		return "illustration_write";
	}
	
	@PostMapping("/art/create")
	public String createArt(@Valid @ModelAttribute CreateArtDTO createArtDTO,HttpServletRequest request, BindingResult bindingResult, Model model, Principal principal) {
		if(bindingResult.hasErrors()) {
			System.out.println("오류 발생");
			return "illustration_write";
		}
		if (request instanceof MultipartHttpServletRequest) {
	        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
	        List<MultipartFile> files = multipartRequest.getFiles("images");
	    }
		if (createArtDTO.getImages() == null || createArtDTO.getImages().isEmpty()) {
	        System.out.println("이미지 리스트가 비어있습니다.");
	    }
		if (createArtDTO.getOriginalContentId()!=null && createArtDTO.getProjectTag() !=null) {
			bindingResult.reject("createArtFailed", "프로젝트에 속하거나 원본 content에 속하거나 하나만 할 수 있습니다");
			return "illustration_write";
		}
		Users user = userService.getUserByUsername(principal.getName());
		
		try {
			contentService.createContentArt(createArtDTO, user);
			//return "redirect:/content/art/list";
			return "redirect:/";
		} catch (IllegalArgumentException e) {
			bindingResult.reject("updateNovelFailed", e.getMessage());
			return "illustration_write";
		} catch (Exception e) {
			System.out.println(e.getMessage());
			bindingResult.reject("createArtFailed", "일러스트를 생성 중 오류가 발생했습니다.");
	        return "illustration_write";
		}	
	}
	@GetMapping("/art/detail/{id}")
	public String artDtail(@PathVariable("id") Long id, Principal principal,  Model model) {
		Content content = contentService.getArt(id);
		if(content == null) {
			return "redirect:/content/art/list";
		}
		List<ContentFile> list = contentFileService.getContentFileByContent(content);
		ResponseArtDetail responseArtDetail = ResponseArtDetail.from(content, list);
		model.addAttribute("art", responseArtDetail);
		List<ResponseTagDTO> contentTags = tagService.getTagForContent(content);
		model.addAttribute("tagList", contentTags);
		return "illustration_detail";
	}
	
	@GetMapping("/art/update/{id}")
	public String updateArt(@PathVariable("id") Long id, Model model) {
		UpdateArtDTO updateArtDTO = UpdateArtDTO.from(contentService.getArt(id));
		Content content = contentService.getArt(id);
		ResponseArtDetail artDetail = ResponseArtDetail.from(content, contentFileService.getContentFileByContent(content));
		model.addAttribute("updateArtDTO", updateArtDTO);
		model.addAttribute("art", artDetail);
		model.addAttribute("id", id);
		return "illustration_edit";
	}
	
	@PostMapping("/art/update/{id}")
	public String updateArt(
	        @PathVariable("id") Long id,
	        @ModelAttribute("updateArtDTO") UpdateArtDTO updateArtDTO,
	        BindingResult bindingResult,
	        Model model) throws IOException {

	    Content content = contentService.getArt(id);

	    System.out.println(updateArtDTO.getNewFiles().size() + "파일 사이즈");
	    if (bindingResult.hasErrors()) {

	        ResponseArtDetail artDetail =
	                ResponseArtDetail.from(
	                        content,
	                        contentFileService.getContentFileByContent(content)
	                );

	        model.addAttribute("art", artDetail);
	        model.addAttribute("id", id);

	        return "illustration_edit";
	    }

	    contentService.updateContentArt(id, updateArtDTO);

	    return "redirect:/content/art/detail/" + id;
	}
	
	@GetMapping("/art/delete/{id}")
	public String deleteArt(@PathVariable("id") Long id, Principal principal) {
		Content content = contentService.getArt(id);
		contentService.delete(content);
		return "redirect:/content/art/list";
	}
	
	@GetMapping("/video/list")
	public String videoList(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
		List<ResponseVideoListDTO> list = contentService.getVideoList(page);
		model.addAttribute("list", list);
		return "video_list";
	}
	
	@GetMapping("/video/create")
	public String createVideo(Model model) {
		CreateVideoDTO createVideoDTO = new CreateVideoDTO();
		model.addAttribute("createVideoDTO", createVideoDTO);
		return "video_write";
	}
	
	@Transactional
	@PostMapping("/video/create")
	public String createVideo(@Valid CreateVideoDTO createVideoDTO, RedirectAttributes redirectAttributes, Principal principal) {
		if(principal==null) {
			redirectAttributes.addFlashAttribute("message", "로그인이 필요한 작업입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/";
		}
		Users user = userService.getUserByUsername(principal.getName());
		try {
			contentService.createContentVideo(createVideoDTO, user);
			redirectAttributes.addFlashAttribute("message", "성공적으로 추가되었습니다!");
			redirectAttributes.addFlashAttribute("icon", "success");
			return "redirect:/content/video/list";
		} catch(Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "오류가 발생했습니다. 문의해주시길 바랍니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/video/create";
		}
	}
	
	@GetMapping("/video/detail/{id}")
	public String videoDetail(@PathVariable("id") Long id, Model model) {
		Content content = contentService.getContent(id);
		ResponseVideoDetailDTO dto = contentService.getVideoDetailById(id);
		model.addAttribute("video", dto);
		List<ResponseTagDTO> contentTags = tagService.getTagForContent(content);
		model.addAttribute("tagList", contentTags);
		return "video_detail";
	}
	
	@GetMapping("/video/edit/{id}")
	public String updateVideo(@PathVariable("id") Long id, Model model, Principal principal, RedirectAttributes redirectAttributes) {
		Content content = contentService.getVideoById(id);
		if(content == null) {
			redirectAttributes.addFlashAttribute("message", "수정하려는 영상을 찾을 수 없습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/";
		}
		if(principal == null) {
			redirectAttributes.addFlashAttribute("message", "로그인이 필요한 작업입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/video/detail/"+id;
		}
		if(!content.getUser().getUsername().equals(principal.getName())) {
			redirectAttributes.addFlashAttribute("message", "오직 본인만 수정할 수 있습니다.");
			redirectAttributes.addFlashAttribute("icon", "warning");
			return "redirect:/content/video/detail/"+id;
		}
		model.addAttribute("updateVideoDTO", UpdateVideoDTO.from(content, contentFileService.getContentFileByContent(content).getFirst()));
		return "video_edit";
	}
	@PostMapping("/video/edit/{id}")
	public String updateVideo(@PathVariable("id") Long id,@Valid UpdateVideoDTO dto, Principal principal, RedirectAttributes redirectAttributes) {
		Content content = contentService.getVideoById(id);
		if(content == null) {
			redirectAttributes.addFlashAttribute("message", "수정하려는 영상을 찾을 수 없습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/";
		}
		if(principal == null) {
			redirectAttributes.addFlashAttribute("message", "로그인이 필요한 작업입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/video/detail/"+id;
		}
		if(!content.getUser().getUsername().equals(principal.getName())) {
			redirectAttributes.addFlashAttribute("message", "오직 본인만 수정할 수 있습니다.");
			redirectAttributes.addFlashAttribute("icon", "warning");
			return "redirect:/content/video/detail/"+id;
		}
		try {
			contentService.updateContentVideo(dto);
			redirectAttributes.addFlashAttribute("message", "성공적으로 수정되었습니다.");
			redirectAttributes.addFlashAttribute("icon", "success");
			return "redirect:/content/video/detail/"+id;
		} catch (IllegalStateException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/video/detail/"+id;
		} catch (Exception e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("message", "알 수 없는 오류가 발생했습니다. 관리자에게 문의바랍니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
	        return "redirect:/content/video/detail/"+id;
	    }
	}
	@PostMapping("/video/delete/{id}")
	public String deleteVideo(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirectAttributes) {
		Content content = contentService.getVideoById(id);
		if(content == null) {
			redirectAttributes.addFlashAttribute("message", "삭제하려는 영상을 찾을 수 없습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/";
		}
		if(principal == null) {
			redirectAttributes.addFlashAttribute("message", "로그인이 필요한 작업입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/video/detail/"+id;
		}
		if(!content.getUser().getUsername().equals(principal.getName())) {
			redirectAttributes.addFlashAttribute("message", "오직 본인만 삭제할 수 있습니다.");
			redirectAttributes.addFlashAttribute("icon", "warning");
			return "redirect:/content/video/detail/"+id;
		}
		contentService.delete(content);
		redirectAttributes.addFlashAttribute("message", "성공적으로 삭제되었습니다.");
		redirectAttributes.addFlashAttribute("icon", "success");
		return "redirect:/content/video/list";
	}
	
	@GetMapping("/music/list")
	public String musicList(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
		List<ResponseMusicListDTO> list = contentService.getMusicList(page);
		model.addAttribute("musicList", list);
		return "music_list";
	}
	
	@GetMapping("/music/write")
	public String createMusic(Model model, Principal principal, RedirectAttributes redirectAttributes) {
		if(principal==null) {
			redirectAttributes.addFlashAttribute("message", "로그인이 필요한 작업입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/";
		}
		CreateMusicDTO dto = new CreateMusicDTO();
		model.addAttribute("createMusicDTO", dto);
		return "/music_write";
	}
	
	@PostMapping("/music/write")
	public String createMusic(@Valid CreateMusicDTO createMusicDTO,Model model, Principal principal, RedirectAttributes redirectAttributes) {
		if(principal==null) {
			redirectAttributes.addFlashAttribute("message", "로그인이 필요한 작업입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/";
		}
		Users user = userService.getUserByUsername(principal.getName());
		try {
			contentService.createContentMusic(createMusicDTO, user);
			redirectAttributes.addFlashAttribute("message", "성공적으로 생성되었습니다.");
			redirectAttributes.addFlashAttribute("icon", "success");
			return "redirect:/content/music/list";
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/music/write";
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "알 수 없는 오류가 발생했습니다. 웹 관리자께 문의바랍니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/music/write";
		}
	}
	
	@GetMapping("/music/detail/{id}")
	public String musicDetail(Model model, @PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		Content content = contentService.getContent(id);
		ResponseMusicDetailDTO dto = contentService.getMusicDetailById(id);
		if(dto == null) {
			redirectAttributes.addFlashAttribute("message", "해당하는 음악을 찾는데 실패했습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/music/list";
		}
		model.addAttribute("music", dto);
		List<ResponseTagDTO> contentTags = tagService.getTagForContent(content);
		model.addAttribute("tagList", contentTags);
		return "music_detail";
	}
	
	@GetMapping("/music/edit/{id}")
	public String updateMusic(Model model, @PathVariable("id") Long id, RedirectAttributes redirectAttributes, Principal principal) {
		Content content = contentService.getContent(id);
		if(content == null) {
			redirectAttributes.addFlashAttribute("message", "해당하는 음악을 찾는데 실패했습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/music/list";
		}
		model.addAttribute("updateMusicDTO", UpdateMusicDTO.from(content, contentFileService.getContentFileByContent(content).getFirst()));
		return "music_edit";
	}
	
	@PostMapping("/music/edit/{id}")
	public String updateMusic(@Valid UpdateMusicDTO dto,Model model, @PathVariable("id") Long id,Principal principal, RedirectAttributes redirectAttributes) {
		Content content = contentService.getContent(id);
		if(content==null) {
			redirectAttributes.addFlashAttribute("message", "해당하는 음악을 찾는데 실패했습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/music/detail/"+id;
		}
		if(principal == null) {
			redirectAttributes.addFlashAttribute("message", "로그인은 필수 사항입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/music/detail/"+id;
		}
		if(!content.getUser().getUsername().equals(principal.getName())) {
			redirectAttributes.addFlashAttribute("message", "오직 본인만 수정할 수 있습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/music/detail/"+id;
		}
		try {
			contentService.updateContentMusic(dto);
			redirectAttributes.addFlashAttribute("message", "성공적으로 수정되었습니다.");
			redirectAttributes.addFlashAttribute("icon", "success");
			return "redirect:/content/music/detail/"+id;
		} catch (IllegalStateException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/music/detail/"+id;
		} catch (Exception e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("message", "알 수 없는 오류가 발생했습니다. 관리자에게 문의바랍니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
	        return "redirect:/content/music/detail/"+id;
	    }
	}
	
	@PostMapping("/music/delete/{id}")
	public String deleteMusic(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirectAttributes) {
		Content content = contentService.getVideoById(id);
		if(content == null) {
			redirectAttributes.addFlashAttribute("message", "삭제하려는 음악을 찾을 수 없습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/";
		}
		if(principal == null) {
			redirectAttributes.addFlashAttribute("message", "로그인이 필요한 작업입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/video/detail/"+id;
		}
		if(!content.getUser().getUsername().equals(principal.getName())) {
			redirectAttributes.addFlashAttribute("message", "오직 본인만 삭제할 수 있습니다.");
			redirectAttributes.addFlashAttribute("icon", "warning");
			return "redirect:/content/video/detail/"+id;
		}
		contentService.delete(content);
		redirectAttributes.addFlashAttribute("message", "성공적으로 삭제되었습니다.");
		redirectAttributes.addFlashAttribute("icon", "success");
		return "redirect:/content/music/list";
	}
}
