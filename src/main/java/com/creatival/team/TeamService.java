package com.creatival.team;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.creatival.FileUtil;
import com.creatival.content.Enum.Visibility;
import com.creatival.tag.Tag;
import com.creatival.tag.TagService;
import com.creatival.team.Enum.ApplicationStatus;
import com.creatival.team.Enum.TeamRole;
import com.creatival.team.dto.CreateTeamApplicationDTO;
import com.creatival.team.dto.CreateTeamDTO;
import com.creatival.team.dto.ResponseTeamApplicationDTO;
import com.creatival.team.dto.ResponseTeamDetailDTO;
import com.creatival.team.dto.ResponseTeamListDTO;
import com.creatival.team.dto.ResponseTeamMember;
import com.creatival.team.dto.UpdateTeamDTO;
import com.creatival.user.Users;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TeamService {
	private final FileUtil fileUtil;
	private final TeamRepository teamRepository;
	private final TagService tagService;
	private final TeamMemberRepository teamMemberRepository;
	private final TeamApplicationRepository teamApplicationRepository;
	
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
}
