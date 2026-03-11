package com.creatival.team;

import java.awt.print.Pageable;
import java.io.IOException;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.creatival.MailService;
import com.creatival.tag.ResponseTagDTO;
import com.creatival.tag.TagService;
import com.creatival.team.dto.CreateProjectDTO;
import com.creatival.team.dto.CreateTeamApplicationDTO;
import com.creatival.team.dto.CreateTeamDTO;
import com.creatival.team.dto.ResponseProjectDeatilDTO;
import com.creatival.team.dto.ResponseProjectListDTO;
import com.creatival.team.dto.ResponseTeamApplicationDTO;
import com.creatival.team.dto.ResponseTeamDetailDTO;
import com.creatival.team.dto.ResponseTeamListDTO;
import com.creatival.team.dto.ResponseTeamMember;
import com.creatival.team.dto.UpdateProjectDTO;
import com.creatival.team.dto.UpdateTeamDTO;
import com.creatival.token.UserToken;
import com.creatival.token.UserTokenService;
import com.creatival.user.UserService;
import com.creatival.user.Users;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;


@RequiredArgsConstructor
@Controller
@RequestMapping("/team")
public class TeamController {
	private final TeamService teamService;
	private final UserService userService;
	private final TagService tagService;
	private final MailService mailService;
	private final UserTokenService userTokenService;
	
	
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
		
		List<ResponseTeamMember> members = teamService.getMemberForTeam(team);
		model.addAttribute("memberList", members);
		
		List<ResponseProjectListDTO> projects = teamService.getProjectList(team);
		model.addAttribute("projectList", projects);
		return "team_detail";
	}
	
	@GetMapping("/{id}/teamApplicationManage")
	public String team_application_manage(Model model, @PathVariable("id") Long id, Principal principal) {
		Users user = userService.getUserByUsername(principal.getName());
		List<ResponseTeamApplicationDTO> list = teamService.getPendingApplications(id, user.getId());
		model.addAttribute("applicationList", list);
		return "team_application_manage";
	}
	
	@GetMapping("/{id}/application")
	public String teamApplication(Model model,@PathVariable("id") Long id,CreateTeamApplicationDTO dto) {
		Team team = teamService.getTeamById(id);
		ResponseTeamDetailDTO responseTeamDetailDTO = ResponseTeamDetailDTO.from(team);
		model.addAttribute("team", responseTeamDetailDTO);
		return "team_application_form";
	}
	
	@PostMapping("/{id}/application")
	public String teamApplication(Model model, @PathVariable("id") Long id, @Valid @ModelAttribute CreateTeamApplicationDTO dto, BindingResult bindingResult, Principal principal) {
		if(bindingResult.hasErrors()) {
			System.out.println("오류 발생");
			return "team_application_form";
		}
		Team team = teamService.getTeamById(id);
		ResponseTeamDetailDTO responseTeamDetailDTO = ResponseTeamDetailDTO.from(team);
		model.addAttribute("team", responseTeamDetailDTO);
		
		Users user = userService.getUserByUsername(principal.getName());
		try {
			teamService.applyToTeam(id, dto, user);
			return "redirect:/team/"+id+"/application/complete";
		} catch (IllegalStateException e) {
			bindingResult.reject("UpdateUserFailed", e.getMessage());
			return "team_application_form";
		} catch (Exception e) {
			e.printStackTrace();
	        bindingResult.reject("UpdateUserFailed", "정보 수정 중 알 수 없는 오류가 발생했습니다.");
	        return "team_application_form";
		}
		
	}
	
	@GetMapping("/{id}/update")
	public String teamUpdate(Model model,@PathVariable("id") Long id, Principal principal) {
		Team team = teamService.getTeamById(id);
		model.addAttribute("teamId", id);
		model.addAttribute("updateTeamDTO", UpdateTeamDTO.from(team));
		return "team_edit";
	}
	
	@PostMapping("/{id}/update")
	public String teamUpdate(@PathVariable("id") Long id ,@Valid @ModelAttribute UpdateTeamDTO dto, BindingResult bindingResult, Principal principal) throws IOException {
		
		if(bindingResult.hasErrors()) {
			System.out.println("오류 발생");
			return "team_edit";
		}
		if(principal.getName()==null) {
			return "redirect:/";
		}
		Users user = userService.getUserByUsername(principal.getName());
		try {
			teamService.updateTeam(id, dto, user.getId());
			return "redirect:/team/"+id;
		} catch (IllegalStateException e) {
			bindingResult.reject("UpdateUserFailed", e.getMessage());
			return "team_edit";
		} catch (Exception e) {
			e.printStackTrace();
	        bindingResult.reject("UpdateTeamFailed", "정보 수정 중 알 수 없는 오류가 발생했습니다.");
	        return "team_edit";
		}
		
		
		
		
	}
	
	@GetMapping("/{id}/application/complete")
	public String teamApplicationComplete() {
		return "team_application_complete";
	}
	
	@PostMapping("/application/{id}/approve")
	public String teamApplicationApprove(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirectAttributes) {
		Users user = userService.getUserByUsername(principal.getName());
		Long teamId = teamService.approveApplication(id, user.getId());
		
		redirectAttributes.addFlashAttribute("message", "신청을 승인했습니다.");
		redirectAttributes.addFlashAttribute("icon", "success");
        return "redirect:/team/" + teamId + "/teamApplicationManage";
	}
	
	@PostMapping("/application/{id}/reject")
	public String teamApplicationReject(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirectAttributes) {
		Users user = userService.getUserByUsername(principal.getName());
		Long teamId = teamService.rejectedApplication(id, user.getId());
		
		redirectAttributes.addFlashAttribute("message", "신청을 거절했습니다.");
		redirectAttributes.addFlashAttribute("icon", "error");
        return "redirect:/team/" + teamId + "/teamApplicationManage";
	}
	
	@GetMapping("/{id}/delete")
	public String teamDelete(Model model,@PathVariable("id") Long id, Principal principal) {
		ResponseTeamDetailDTO dto = teamService.getTeamDetail(id);
		model.addAttribute("team", dto);
		return "team_delete";
	}
	
	@PostMapping("/{id}/delete")
	public String teamDelete(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirectAttributes) {
		if(principal.getName()==null) {
			redirectAttributes.addFlashAttribute("message", "로그인이 필요한 작업입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/";
		}
		
		Team team = teamService.getTeamById(id);
		Users user = userService.getUserByUsername(principal.getName());
		if(team.getUser().getId()!=user.getId()) {
			redirectAttributes.addFlashAttribute("message", "팀 해체는 팀장만 할 수 있습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/";
		}
		teamService.delete(id);
		redirectAttributes.addFlashAttribute("message", "팀이 해체되었습니다.");
		redirectAttributes.addFlashAttribute("icon", "success");
		return "redirect:/team/list";
	}
	
	@GetMapping("/{id}/project/create")
	public String createTeamProject(Model model,@PathVariable("id") Long id, Principal principal,CreateProjectDTO createProjectDTO, RedirectAttributes redirectAttributes) {
		Team team = teamService.getTeamById(id);
		if(principal==null) {
			redirectAttributes.addFlashAttribute("message", "로그인이 필요한 작업입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/team/"+id;
		}
		Users user = userService.getUserByUsername(principal.getName());
		if(!team.getUser().getId().equals(user.getId())) {
			redirectAttributes.addFlashAttribute("message", "프로젝트 생성은 팀장만 할 수 있습니다.");
			redirectAttributes.addFlashAttribute("icon", "warging");
			return "redirect:/team/"+id;
		}
		model.addAttribute("teamId", team.getId());
		return "team_project_write";
	}
	
	@PostMapping("/{id}/project/create")
	public String createTeamProjectPost(Model model,@PathVariable("id") Long id, Principal principal, @Valid @ModelAttribute CreateProjectDTO createProjectDTO, RedirectAttributes redirectAttributes) {
		Team team = teamService.getTeamById(id);
		
		model.addAttribute("teamId", team.getId());
		if(principal==null) {
			redirectAttributes.addFlashAttribute("message", "로그인이 필요한 작업입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/team/"+id+"/project/create";
		}
		Users user = userService.getUserByUsername(principal.getName());
		if(!team.getUser().getId().equals(user.getId())) {
			redirectAttributes.addFlashAttribute("message", "프로젝트 생성은 팀장만 할 수 있습니다.");
			redirectAttributes.addFlashAttribute("icon", "warging");
			return "redirect:/team/"+id+"/project/create";
		}
		
		try {
			teamService.createProject(createProjectDTO, team);
			return "redirect:/team/"+id;
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "알 수 없는 오류가 발생했습니다. 만일 동일한 오류가 계속 발견된다면 문의바랍니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/team/"+id+"/project/create";
		}
		
	}
	
	@GetMapping("/{id}/project/list")
	public String projectList(Model model, @PathVariable("id") Long id, @RequestParam(value = "page", defaultValue = "0") int page) {
		Team team = teamService.getTeamById(id);
		Page<ResponseProjectListDTO> list = teamService.getProjectList(page, team);
		model.addAttribute("paging", list);
		model.addAttribute("teamId", id);
		return "team_project_list";
	}
	
	@GetMapping("/{tid}/project/{pid}")
	public String projectDetail(Model model, @PathVariable("tid") Long teamId, @PathVariable("pid") Long projectId) {
		Project project = teamService.getProjectById(projectId);
		model.addAttribute("dto", ResponseProjectDeatilDTO.from(project));
		return "team_project_detail";
	}
	
	@GetMapping("/{tid}/project/{pid}/edit")
	public String projectUpdate(Model model, @PathVariable("tid") Long teamId, @PathVariable("pid") Long projectId, Principal principal, RedirectAttributes redirectAttributes) {
		Team team = teamService.getTeamById(teamId);
		if(principal==null) {
			redirectAttributes.addFlashAttribute("message", "프로젝트를 수정할려면 로그인을 해야합니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/team/"+teamId+"/project/"+projectId;
		}
		if(!team.getUser().getUsername().equals(principal.getName())) {
			redirectAttributes.addFlashAttribute("message", "오직 팀장만이 프로젝트를 수정할 수 있습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/team/"+teamId+"/project/"+projectId;
		}
		Project project = teamService.getProjectById(projectId);
		if(project == null) {
			redirectAttributes.addFlashAttribute("message", "프로젝트를 발견하지 못 했습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/team/"+teamId+"/project/"+projectId;
		}
		UpdateProjectDTO dto = UpdateProjectDTO.from(project);
		model.addAttribute("teamId", teamId);
		model.addAttribute("dto", dto);
		return "team_project_edit";
	}
	
	@PostMapping("/{tid}/project/{pid}/edit")
	public String projectUdate(Model model,@Valid @ModelAttribute(name = "dto") UpdateProjectDTO dto, @PathVariable("tid") Long teamId, @PathVariable("pid") Long projectId, RedirectAttributes redirectAttributes) {
		Team team = teamService.getTeamById(teamId);
		
		try {
			teamService.updateProject(dto, team, projectId);
			redirectAttributes.addFlashAttribute("message", "성공적으로 수정되었습니다.");
			redirectAttributes.addFlashAttribute("icon", "success");
			return "redirect:/team/"+teamId+"/project/"+projectId;
		} catch (IllegalStateException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/team/"+teamId+"/project/"+projectId+"/edit";
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "예기치 못 한 오류가 발생했습니다. 계속되면 문의를 주시길 바랍니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/team/"+teamId+"/project/"+projectId+"/edit";
		}
		
	}
	@GetMapping("/{id}/teamMemberManage")
	public String teamMemberManage(Model model,@PathVariable("id") Long id) {
		Team team = teamService.getTeamById(id);
		
		model.addAttribute("leaderUsername", team.getUser().getUsername());
		model.addAttribute("teamId", team.getId());
		
		List<ResponseTeamMember> members = teamService.getMemberForTeam(team);
		model.addAttribute("memberList", members);
		return "team_member_manage";
	}
	
	@PostMapping("{id}/member/changePosition")
	public String memberChangePosition(@PathVariable("id") Long id, @RequestParam("memberId") Long memberId, @RequestParam("position") String position, RedirectAttributes redirectAttributes) {
		TeamMember member = teamService.getMemberForTeamById(memberId);
		if(member==null) {
			redirectAttributes.addFlashAttribute("message", "해당하는 멤버를 찾지 못 했습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/team/"+id+"/teamMemberManage";
		}
		teamService.memberChangePosition(member, position);
		return "redirect:/team/"+id+"/teamMemberManage";
	}
	
	@GetMapping("/member/delete/{memberId}")
	public String deleteMember(@PathVariable("memberId") Long id, @RequestParam(name = "message") String message, Principal principal, RedirectAttributes redirectAttributes) throws MessagingException {
		TeamMember member = teamService.getMemberForTeamById(id);
		Team team = member.getTeam();
		if(principal==null) {
			redirectAttributes.addFlashAttribute("message", "팀원을 탈퇴시키는 행위는 로그인이 필요합니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/team/"+id+"/teamMemberManage";
		}
		
		Users user = member.getUser();
		
		if(!team.getUser().getUsername().equals(principal.getName()) && !user.getUsername().equals(principal.getName())) {
			redirectAttributes.addFlashAttribute("message", "오직 팀의 팀장만 팀원을 탈퇴시킬 수 있습니다!");
			redirectAttributes.addFlashAttribute("icon", "warning");
			return "redirect:/team/"+id+"/teamMemberManage";
		}
		if(team.getUser().getUsername().equals(principal.getName())) {
			teamService.deleteMember(member, message);
			redirectAttributes.addFlashAttribute("message", "팀원이 탈퇴처리되었습니다. 작성된 사유는 메일로 보내졌습니다.");
			redirectAttributes.addFlashAttribute("icon", "success");
			return "redirect:/team/"+id+"/teamMemberManage";
		}
		if(user.getUsername().equals(principal.getName())) {
			teamService.leaveTeam(member, message);
			redirectAttributes.addFlashAttribute("message", "정상적으로 탈퇴처리되었습니다. 작성된 사유는 메일로 보내졌습니다.");
			redirectAttributes.addFlashAttribute("icon", "success");
			return "redirect:/";
		}
		
		
		
		redirectAttributes.addFlashAttribute("message", "알 수 없는 오류가 발생하였습니다.");
		redirectAttributes.addFlashAttribute("icon", "error");
		return "redirect:/team/"+id+"/teamMemberManage";
	}
	
	@PostMapping("/member/role")
	public String changeTeamLeader(@RequestParam(name = "memberId") Long memberId, Principal principal, RedirectAttributes redirectAttributes) throws MessagingException {
		TeamMember member = teamService.getMemberForTeamById(memberId);
		Team team = member.getTeam();
		if(principal==null) {
			redirectAttributes.addFlashAttribute("message", "해당 작업은 로그인이 필요합니다!");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/team/"+team.getId()+"/teamMemberManage";
		}
		if(!team.getUser().getUsername().equals(principal.getName())) {
			redirectAttributes.addFlashAttribute("message", "오직 팀의 팀장만 팀장을 변경할 수 있습니다.");
			redirectAttributes.addFlashAttribute("icon", "warning");
			return "redirect:/team/"+team.getId()+"/teamMemberManage";
		}
		
		String link = teamService.createChangeTeamLeaderLink(member.getUser(), team);
		mailService.changeTeamLeader(member.getUser().getEmail(), link);
		redirectAttributes.addFlashAttribute("message", "성공적으로 팀장 변경 메세지를 전달하였습니다.");
		redirectAttributes.addFlashAttribute("icon", "success");
		return "redirect:/team/"+team.getId();
	}
	
	@GetMapping("/member/changeLeader")
	public String changeTeamLeader(@RequestParam("token") String token, @RequestParam("teamId") Long teamId,Principal principal,RedirectAttributes redirectAttributes) {
		UserToken token2 = userTokenService.getUserTokenByToken(token);
		if(token2 == null) {
			redirectAttributes.addFlashAttribute("message", "토큰이 존재하지 않습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/";
		}
		if(principal == null) {
			redirectAttributes.addFlashAttribute("message", "로그인이 필요한 작업입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/";
		}
		if(!token2.getUser().getUsername().equals(principal.getName())) {
			redirectAttributes.addFlashAttribute("message", "귀하를 위한 토큰이 아닙니다!");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/";
		}
		Team team = teamService.getTeamById(teamId);
		if(teamService.getMemberForTeamByUser(team, token2.getUser()) == null) {
			redirectAttributes.addFlashAttribute("message", "오직 멤버만 팀장이 될 수 있습니다!");
			redirectAttributes.addFlashAttribute("icon", "warning");
			return "redirect:/";
		}
		TeamMember member = teamService.getMemberForTeamByUser(team, token2.getUser());
		TeamMember leader = teamService.getMemberForTeamByUser(team, team.getUser()); // Leader은 바뀔 기존 리더를 의미함
		try {
			teamService.changeTeamLeader(member,leader, team);
			redirectAttributes.addFlashAttribute("message", "성공적으로 변경되었습니다!");
			redirectAttributes.addFlashAttribute("icon", "success");
			userTokenService.deleteToken(token2);
			return "redirect:/team/"+team.getId();
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "알 수 없는 오류가 났습니다. 사이트 관리자에게 문의바랍니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/team/"+team.getId();
		}
	}
	
}
