package com.creatival.team;

import java.io.IOException;
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
import com.creatival.team.Enum.TeamRole;
import com.creatival.team.dto.CreateTeamDTO;
import com.creatival.team.dto.ResponseTeamDetailDTO;
import com.creatival.team.dto.ResponseTeamListDTO;
import com.creatival.user.Users;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TeamService {
	private final FileUtil fileUtil;
	private final TeamRepository teamRepository;
	private final TagService tagService;
	private final TeamMemberRepository teamMemberRepository;
	
	public Team getTeamById(Long id) {
		return teamRepository.findById(id).get();
	}
	
	public void createTeam(CreateTeamDTO createTeamDTO, Users user) throws IOException {
		String profileImgurl = "/upload/images/thumbnail/";
		String banner = "/upload/images/banner/";
		if(createTeamDTO.getProfileImage() != null) {
			profileImgurl += fileUtil.saveImage(createTeamDTO.getProfileImage(), "thumbnail");
		}
		if(createTeamDTO.getBannerImage() != null) {
			banner += fileUtil.saveImage(createTeamDTO.getBannerImage(), "banner");
		}
		
		Team team = Team.builder()
				.user(user)
				.name(createTeamDTO.getName())
				.description(createTeamDTO.getDescription())
				.visibility(createTeamDTO.getVisibility())
				.profileImgUrl(profileImgurl)
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
}
