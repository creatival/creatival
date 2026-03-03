package com.creatival.team;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.creatival.tag.ResponseTagDTO;
import com.creatival.tag.TagService;
import com.creatival.team.dto.CreateTeamDTO;
import com.creatival.team.dto.ResponseTeamDetailDTO;
import com.creatival.team.dto.ResponseTeamListDTO;
import com.creatival.user.UserService;
import com.creatival.user.Users;

import jakarta.validation.Valid;


@RequiredArgsConstructor
@Controller
@RequestMapping("/team")
public class TeamController {
	private final TeamService teamService;
	private final UserService userService;
	private final TagService tagService;
	
	
	@GetMapping("/list")
	public String teamList(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
		Page<ResponseTeamListDTO> paging = teamService.getTeamList(page);
		model.addAttribute("paging", paging);
		return "team_list";
	}
	
	@GetMapping("/create")
	public String createTeam(CreateTeamDTO createTeamDTO) {
		return "team_write";
	}
	
	@PostMapping("/create")
	public String createTeam(@Valid @ModelAttribute CreateTeamDTO createTeamDTO,BindingResult bindingResult, Principal principal) {
		if(bindingResult.hasErrors()) {
			System.out.println("오류 발생");
			return "team_write";
		}
		if(principal.getName() == null) {
			return "redirect:/user/login";
		}
		Users user = userService.getUserByUsername(principal.getName());
		try {
			teamService.createTeam(createTeamDTO, user);
			return "redirect:/team/list";
		} catch (Exception e) {
			e.printStackTrace();
	        bindingResult.reject("createNovelFailed", "팀을 생성하던 중 문제가 발생했습니다.");
	        return "team_write";
		}
		
	}
	
	@GetMapping("/{id}")
	public String teamDetail(Model model,@PathVariable("id") Long id) {
		ResponseTeamDetailDTO dto = teamService.getTeamDetail(id);
		model.addAttribute("team", dto);
		
		Team team = teamService.getTeamById(id);
		List<ResponseTagDTO> tags = tagService.getTagForTeam(team);
		model.addAttribute("tagList", tags);
		return "team_detail";
	}
	
	@GetMapping("/team/{id}/teamApplication")
	public String team_application_manage(Model model, @PathVariable("id") Long id) {
		return "team_application_manage";
	}
}
