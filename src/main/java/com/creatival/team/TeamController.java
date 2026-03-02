package com.creatival.team;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.creatival.team.DTO.CreateTeamDTO;
import com.creatival.user.UserService;
import com.creatival.user.Users;

import jakarta.validation.Valid;


@RequiredArgsConstructor
@Controller
@RequestMapping("/team")
public class TeamController {
	private final TeamService teamService;
	private final UserService userService;
	
	
	@GetMapping("/list")
	public String teamList() {
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
	
}
