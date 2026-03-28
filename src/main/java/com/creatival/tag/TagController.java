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

import com.creatival.content.Content;
import com.creatival.content.ContentFileService;
import com.creatival.content.ContentService;
import com.creatival.team.Team;
import com.creatival.team.TeamService;
import com.creatival.user.UserService;
import com.creatival.user.Users;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/tag")
public class TagController {
	private final TagService tagService;
	private final UserService userService;
	private final ContentService contentService;
	private final TeamService teamService;
	
	@PostMapping("/createUserTag")
	public String createTagForUser(@RequestParam("tagName") String tagName, Principal principal) {
		Users user = userService.getUserByUsername(principal.getName());
		tagService.createTagForUser(user, tagName);
		return "redirect:/user/myPage";
	}
	
	@PostMapping("/createContentNovelTag/{id}")
	public String createTagForNovel(@RequestParam("tagName") String tagName, @PathVariable("id") Long id, Principal principal) {
		createTagForContent(tagName, id, principal.getName());
		return "redirect:/content/novel_detail/"+id;
	}
	@PostMapping("/createContentArtTag/{id}")
	public String createTagForArt(@RequestParam("tagName") String tagName, @PathVariable("id") Long id, Principal principal) {
		createTagForContent(tagName, id, principal.getName());
		return "redirect:/content/art/detail/"+id;
	}
	@PostMapping("/createContentVideoTag/{id}")
	public String createTagForVideo(@RequestParam("tagName") String tagName, @PathVariable("id") Long id, Principal principal) {
		createTagForContent(tagName, id, principal.getName());
		return "redirect:/content/video/detail/"+id;
	}
	@PostMapping("/createContentMusicTag/{id}")
	public String createTagForMusic(@RequestParam("tagName") String tagName, @PathVariable("id") Long id, Principal principal) {
		createTagForContent(tagName, id, principal.getName());
		return "redirect:/content/music/detail/"+id;
	}
	
	private void createTagForContent(String tagName, Long id, String username) {
		Content content = contentService.getContent(id);
		tagService.createTagForContent(content, tagName, username);
	}
	
	@GetMapping("/deleteUserTag")
	public String deleteUserTag(@RequestParam("tagId") Long tagId, Principal principal) {
	    
	    Users user = userService.getUserByUsername(principal.getName());
	    Long userId = user.getId();
	    
	    // 서비스에 삭제 로직 위임
	    tagService.deleteMappingForUser(userId, tagId);
	    
	    return "redirect:/user/myPage"; // 삭제 후 다시 마이페이지로
	}
	
	@GetMapping("/novel/deleteContentTag")
	public String deleteContentNovelTag(@RequestParam("tagId") Long tagId, @RequestParam("contentId") Long contentId) {
		tagService.deleteMappingForContent(contentId, tagId);
		
		return "redirect:/content/novel_detail/"+contentId;
	}
	@GetMapping("/art/deleteContentTag")
	public String deleteContentArtTag(@RequestParam("tagId") Long tagId, @RequestParam("contentId") Long contentId) {
		tagService.deleteMappingForContent(contentId, tagId);
		
		return "redirect:/content/art/detail/"+contentId;
	}
	@GetMapping("/video/deleteContentTag")
	public String deleteContentVideoTag(@RequestParam("tagId") Long tagId, @RequestParam("contentId") Long contentId) {
		tagService.deleteMappingForContent(contentId, tagId);
		
		return "redirect:/content/video/detail/"+contentId;
	}
	@GetMapping("/music/deleteContentTag")
	public String deleteContentMusicTag(@RequestParam("tagId") Long tagId, @RequestParam("contentId") Long contentId) {
		tagService.deleteMappingForContent(contentId, tagId);
		
		return "redirect:/content/music/detail/"+contentId;
	}
	
	@GetMapping("/team/deleteTeamTag")
	public String deleteTeamTag(@RequestParam("tagId") Long tagId, @RequestParam("teamId") Long teamId) {
		tagService.deleteMappingForTeam(teamId, tagId);
		
		return "redirect:/team/"+teamId;
	}
	
	@PostMapping("/team/createTeamTag/{id}")
	public String createTeamTag(@RequestParam("tagName") String tagName,@PathVariable("id") Long id, Principal principal) {
		Users user = userService.getUserByUsername(principal.getName());
		Team team = teamService.getTeamById(id);
		tagService.createTagForTeam(team, tagName, user);
		return "redirect:/team/"+id;
	}
}
