package com.creatival.team;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.creatival.FileUtil;
import com.creatival.MailService;
import com.creatival.content.Enum.Visibility;
import com.creatival.tag.Tag;
import com.creatival.tag.TagService;
import com.creatival.team.Enum.ApplicationStatus;
import com.creatival.team.Enum.TeamRole;
import com.creatival.team.dto.CreateProjectDTO;
import com.creatival.team.dto.CreateTeamApplicationDTO;
import com.creatival.team.dto.CreateTeamDTO;
import com.creatival.team.dto.ResponseProjectListDTO;
import com.creatival.team.dto.ResponseTeamApplicationDTO;
import com.creatival.team.dto.ResponseTeamDetailDTO;
import com.creatival.team.dto.ResponseTeamListDTO;
import com.creatival.team.dto.ResponseTeamMember;
import com.creatival.team.dto.UpdateProjectDTO;
import com.creatival.team.dto.UpdateTeamDTO;
import com.creatival.team.repository.*;
import com.creatival.token.UserToken;
import com.creatival.token.UserTokenService;
import com.creatival.user.UserService;
import com.creatival.user.Users;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TeamService {

    private final CheckoutRepository checkoutRepository;

    private final UserTokenService userTokenService;

    private final MailService mailService;
	private final FileUtil fileUtil;
	private final TeamRepository teamRepository;
	private final TagService tagService;
	private final TeamMemberRepository teamMemberRepository;
	private final TeamApplicationRepository teamApplicationRepository;
	private final ProjectRepository projectRepository;
	private final UserService userService;


	
	public Team getTeamById(Long id) {
		return teamRepository.findById(id).get();
	}
	
	public void createTeam(CreateTeamDTO createTeamDTO, Users user) throws IOException {
		String profileImgUrl = "/upload/images/thumbnail/";
		String banner = "/upload/images/banner/";
		if(createTeamDTO.getProfileImage() != null) {
			profileImgUrl += fileUtil.saveImage(createTeamDTO.getProfileImage(), "thumbnail");
		}
		if(createTeamDTO.getBannerImage() != null) {
			banner += fileUtil.saveImage(createTeamDTO.getBannerImage(), "banner");
		}
		
		Team team = Team.builder()
				.user(user)
				.name(createTeamDTO.getName())
				.description(createTeamDTO.getDescription())
				.visibility(createTeamDTO.getVisibility())
				.profileImgUrl(profileImgUrl)
				.bannerImgUrl(banner)
				.build();
		teamRepository.save(team);
		
		if (createTeamDTO.getTags() != null) {
		    for (String tagName : createTeamDTO.getTags()) {

		        tagService.createTagForTeam(team, tagName, user);
		    }
		}
		
		TeamMember teamMember = TeamMember.builder()
				.user(user)
				.team(team)
				.role(TeamRole.LEADER)
				.position("팀장")
				.build();
		
		teamMemberRepository.save(teamMember);
	}
	
	public Page<ResponseTeamListDTO> getTeamList(int page) {
		Pageable pageable = PageRequest.of(page, 12, Sort.by("createdAt").descending());
		Page<Team> teams = teamRepository.findByVisibility(Visibility.PUBLIC, pageable);
		return teams.map(team -> ResponseTeamListDTO.from(team));
	}
	
	
	
	

	public ResponseTeamDetailDTO getTeamDetail(Long id) {
		Optional<Team> team = teamRepository.findById(id);
		if(team.isEmpty()) {
			return null;
		}
		return ResponseTeamDetailDTO.from(team.get());
	}
	
	public void applyToTeam(Long teamId, CreateTeamApplicationDTO dto, Users user) {
		Team team = teamRepository.findById(teamId)
	            .orElseThrow(() -> new IllegalArgumentException("팀이 존재하지 않습니다."));
		
		if (teamApplicationRepository.existsByTeamAndUserAndStatus(team, user, ApplicationStatus.PENDING)) {
	        throw new IllegalStateException("이미 신청 중입니다.");
	    }
		if (teamMemberRepository.existsByTeamAndUser(team, user)) {
			throw new IllegalStateException("이미 소속되어있습니다.");
		}
		
		 TeamApplication application = TeamApplication.builder()
		            .team(team)
		            .user(user)
		            .status(ApplicationStatus.PENDING)
		            .message(dto.getMessage())
		            .build();
		 
		 teamApplicationRepository.save(application);
		 
	}

	public List<ResponseTeamApplicationDTO> getPendingApplications(Long teamId, Long loginUserId) {
	    Team team = teamRepository.findById(teamId)
	            .orElseThrow(() -> new IllegalArgumentException("팀이 존재하지 않습니다."));

	    if (!team.getUser().getId().equals(loginUserId)) {
	        throw new IllegalStateException("팀장만 신청 목록을 조회할 수 있습니다.");
	    }

	    List<TeamApplication> applications =
	            teamApplicationRepository.findByTeamIdAndStatus(teamId, ApplicationStatus.PENDING);

	    return applications.stream()
	            .map(app -> ResponseTeamApplicationDTO.builder()
	                    .id(app.getId())
	                    .teamId(teamId)
	                    .userId(app.getUser().getId())
	                    .username(app.getUser().getUsername())
	                    .message(app.getMessage())
	                    .status(app.getStatus())
	                    .createdAt(app.getCreatedAt())
	                    .build())
	            .toList();
	}

	@Transactional
	public Long approveApplication(Long id, Long userId) {
		TeamApplication application = teamApplicationRepository.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("신청이 존재하지 않습니다."));
		Team team = application.getTeam();
		if (!team.getUser().getId().equals(userId)) {
	        throw new IllegalStateException("팀장만 승인할 수 있습니다.");
	    }

	    if (application.getStatus() != ApplicationStatus.PENDING) {
	        throw new IllegalStateException("이미 처리된 신청입니다.");
	    }
	    
	    TeamMember member = TeamMember.builder()
	    		.team(team)
	    		.user(application.getUser())
	    		.role(TeamRole.MEMBER)
	    		.position("팀원")
	    		.build();
	    
	    teamMemberRepository.save(member);
	    
	    application.setStatus(ApplicationStatus.APPROVED);
	    
	    teamApplicationRepository.save(application);
	    return application.getTeam().getId();
	}
	@Transactional
	public Long rejectedApplication(Long id, Long userId) {
		TeamApplication application = teamApplicationRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("신청이 존재하지 않습니다."));
		Team team = application.getTeam();
		if (!team.getUser().getId().equals(userId)) {
			throw new IllegalStateException("팀장만 승인할 수 있습니다.");
		}
		
		if (application.getStatus() != ApplicationStatus.PENDING) {
			throw new IllegalStateException("이미 처리된 신청입니다.");
		}
		
		application.setStatus(ApplicationStatus.REJECTED);
		
		teamApplicationRepository.save(application);
		return application.getTeam().getId();
	}

	public List<ResponseTeamMember> getMemberForTeam(Team team) {
		List<TeamMember> list = teamMemberRepository.findByTeam(team);
		return list.stream()
	            .map(member -> ResponseTeamMember.from(member)).toList();
	}

	@Transactional
	public void updateTeam(Long id, @Valid UpdateTeamDTO dto, Long userId) throws IOException {
		String profileImgUrl = "/upload/images/thumbnail/";
		String banner = "/upload/images/banner/";
		
		Team team = teamRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("팀이 존재하지 않습니다."));
		if (!team.getUser().getId().equals(userId)) {
	        throw new IllegalStateException("팀장만 수정할 수 있습니다.");
	    }
		if(!dto.getProfileImg().isEmpty()) {
			profileImgUrl += fileUtil.saveImage(dto.getProfileImg(), "thumbnail");
			team.setProfileImgUrl(profileImgUrl);
		}
		if(!dto.getBannerImg().isEmpty()) {
			banner += fileUtil.saveImage(dto.getBannerImg(), "banner");
			team.setBannerImgUrl(banner);
		}
		team.setName(dto.getName());
		team.setDescription(dto.getDescription());
		team.setStatus(dto.getStatus());
		team.setVisibility(dto.getVisibility());
		
		teamRepository.save(team);
	}

	public void delete(Long teamId) {
		Optional<Team> team = teamRepository.findById(teamId);
		if(team.isEmpty()) {
			return;
		}
		teamRepository.delete(team.get());
		
	}

	public void createProject(@Valid CreateProjectDTO createProjectDTO, Team team) throws IOException {
		String banner = "/upload/images/banner/";
		if(createProjectDTO.getBannerImg() != null) {
			banner += fileUtil.saveImage(createProjectDTO.getBannerImg(), "banner");
		}
		Project project = Project.builder()
				.title(createProjectDTO.getTitle())
				.description(createProjectDTO.getDescription())
				.visibility(createProjectDTO.getVisibility())
				.endDate(createProjectDTO.getEndDate())
				.bannerImgUrl(banner)
				.projectTag(createProjectDTO.getProjectTag())
				.team(team)
				.build();
		projectRepository.save(project);
		
		if (createProjectDTO.getTags() != null) {
		    for (String tagName : createProjectDTO.getTags()) {

		        tagService.createTagForProject(project, tagName, team.getUser());
		    }
		}
	}
	
	public List<ResponseProjectListDTO> getProjectList(Team team) {
		List<Project> projects = projectRepository.findByTeamAndVisibility(team,Visibility.PUBLIC);
		System.out.println("조회된 프로젝트 개수: " + projects.size());
		return projects.stream().map(project -> ResponseProjectListDTO.from(project)).toList();
	}
	
	public Page<ResponseProjectListDTO> getProjectList(int page, Team team) {
		Pageable pageable = PageRequest.of(page, 12, Sort.by("createdAt").descending());
		Page<Project> projects = projectRepository.findByTeamAndVisibility(team, Visibility.PUBLIC, pageable);
		return projects.map(project -> ResponseProjectListDTO.from(project));
	}

	public Project getProjectById(Long projectId) {
		Optional<Project> project = projectRepository.findById(projectId);
		return project.isPresent() ? project.get() : null;
	}

	public void updateProject(@Valid UpdateProjectDTO dto, Team team, Long projectId) throws IOException {
		String banner = "/upload/images/banner/";
		System.out.println(dto.getId());
		Optional<Project> optional = projectRepository.findById(projectId);
		if(optional.isEmpty()) {
			throw new IllegalStateException("수정할려는 프로젝트를 찾을 수 없습니다.");
		}
		Project project = optional.get();
		
		project.setTitle(dto.getTitle());
		project.setDescription(dto.getDescription());
		project.setEndDate(dto.getEndDate());
		project.setVisibility(dto.getVisibility());
		project.setStatus(dto.getStatus());
		
		if(dto.getBannerImg() != null && !dto.getBannerImg().isEmpty()) {
			banner += fileUtil.saveImage(dto.getBannerImg(), "banner");
			project.setBannerImgUrl(banner);
		}
		
		projectRepository.save(project);
		
	}

	public TeamMember getMemberForTeamByUser(Team team, Users user) {
		Optional<TeamMember> teamMember = teamMemberRepository.findByTeamAndUser(team, user);
		if(teamMember.isEmpty()) {
			return null;
		}
		return teamMember.get();
	}

	public void memberChangePosition(TeamMember member, String position) {
		member.setPosition(position);
		teamMemberRepository.save(member);
	}

	public TeamMember getMemberForTeamById(Long memberId) {
		Optional<TeamMember> teamMember = teamMemberRepository.findById(memberId);
		if(teamMember.isEmpty()) {
			return null;
		}
		return teamMember.get();
	}

	public void deleteMember(TeamMember member, String message) throws MessagingException {
		teamMemberRepository.delete(member);
		mailService.deleteTeamMemberMail(member.getUser().getEmail(), member, message);
	}

	public void leaveTeam(TeamMember member, String message) throws MessagingException {
		teamMemberRepository.delete(member);
		mailService.leaveTeamMemberMail(member.getTeam().getUser().getEmail(), member, message);
		
	}
	//Leader은 바뀔 팀의 기존 리더를 의미함
	public void changeTeamLeader(TeamMember member, TeamMember leader, Team team) {
		member.setRole(TeamRole.LEADER);
		member.setPosition("팀장");
		
		leader.setRole(TeamRole.MEMBER);
		leader.setPosition("팀원");
		
		team.setUser(member.getUser());
		
		teamMemberRepository.save(member);
		teamMemberRepository.save(leader);
		
		teamRepository.save(team);
	}
	
	public String createChangeTeamLeaderLink(Users user, Team team) {
		UserToken token = userTokenService.createUserToken(user);
		
		return "http://localhost:8080/team/member/changeLeader?token=" + token.getToken() + "&teamId="+team.getId();
	}

	public Project getProjectTag(String projectTag) {
		Optional<Project> project = projectRepository.findByProjectTag(projectTag);
		if(project.isPresent()) {
			return project.get();
		}
		return null;
		
	}
	
	public void createCheckout(Project project, String title, LocalDateTime deadline) {
		Checkout checkout = new Checkout();
		checkout.setTitle(title);
		checkout.setProject(project);
		
		int max = checkoutRepository.findMaxSortOrder(project.getId());

		checkout.setSortOrder(max + 1);
		
		if(deadline != null) {
			checkout.setDeadline(deadline);
		}
		
		checkoutRepository.save(checkout);
	}
}
