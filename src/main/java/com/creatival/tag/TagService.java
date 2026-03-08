package com.creatival.tag;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.creatival.FileUtil;
import com.creatival.content.Content;
import com.creatival.content.repository.ContentFileRepository;
import com.creatival.content.repository.ContentRepository;
import com.creatival.tag.repository.TagRepository;
import com.creatival.tag.repository.TagToContentRepository;
import com.creatival.tag.repository.TagToProjectRepository;
import com.creatival.tag.repository.TagToTeamRepository;
import com.creatival.tag.repository.TagToUsersRepository;
import com.creatival.team.Project;
import com.creatival.team.Team;
import com.creatival.user.UserService;
import com.creatival.user.Users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TagService {

    private final TagToTeamRepository tagToTeamRepository;

    private final TagToContentRepository tagToContentRepository;

    private final UserService userService;
	private final TagRepository tagRepository;
	private final TagToUsersRepository tagToUsersRepository;
	private final TagToProjectRepository tagToProjectRepository;

	
	public void createTagForUser(Users user, String tagName) {
		Tag tag;
		if(tagRepository.findByTagText(tagName).isEmpty()) {
			tag = Tag.builder().tagText(tagName).user(user).build();
			tagRepository.save(tag);
		} else {
			tag = tagRepository.findByTagText(tagName).get();
		}
		
		TagToUsers tagToUsers = TagToUsers.from(user, tag);
		tagToUsersRepository.save(tagToUsers);
	}
	
	public void createTagForContent(Content content, String tagName, String username) {
		Tag tag;
		Users user = userService.getUserByUsername(username);
		if(tagRepository.findByTagText(tagName).isEmpty()) {
			tag = Tag.builder().tagText(tagName).user(user).build();
			tagRepository.save(tag);
		} else {
			tag = tagRepository.findByTagText(tagName).get();
		}
		
		TagToContent tagToContent = TagToContent.from(content, tag);
		tagToContentRepository .save(tagToContent);
	}
	
	public List<ResponseTagDTO> getTagForUser(Users user){
		List<TagToUsers> mappings = tagToUsersRepository.findByUser(user);
		return mappings.stream()
	            .map(m -> new ResponseTagDTO(
	                    m.getTag().getId(), 
	                    m.getTag().getTagText()
	                ))
	                .collect(Collectors.toList());
	}
	
	public List<ResponseTagDTO> getTagForTeam(Team team){
		List<TagToTeam> mappings = tagToTeamRepository.findByTeam(team);
		return mappings.stream()
	            .map(m -> new ResponseTagDTO(
	                    m.getTag().getId(), 
	                    m.getTag().getTagText()
	                ))
	                .collect(Collectors.toList());
	}
	
	public List<ResponseTagDTO> getTagForContent(Content content) {
		List<TagToContent> mappings = tagToContentRepository.findByContent(content);
		return mappings.stream()
				.map(m -> new ResponseTagDTO(
						m.getTag().getId(),
						m.getTag().getTagText()
						))
				.collect(Collectors.toList());
	}
	
	@Transactional
	public void deleteMappingForUser(Long userId, Long tagId) {

	    // 2. UserID와 TagID로 매핑 데이터 삭제
	    tagToUsersRepository.deleteByUserIdAndTagId(userId, tagId);
	}
	
	@Transactional
	public void deleteMappingForContent(Long contentId, Long tagId) {

	    // 2. UserID와 TagID로 매핑 데이터 삭제
	    tagToContentRepository.deleteByContentIdAndTagId(contentId, tagId);
	}
	
	public void deleteTagForDeleteUser(Users user) {
		tagToUsersRepository.deleteByUser(user);
	}
	public void deleteTagForDeleteContent(Content content) {
		tagToContentRepository.deleteByContent(content);
	}
	//나중에 공통 로직 정리 필요
	public void createTagForTeam(Team team, String tagName, Users user) {
		Tag tag;
		if(tagRepository.findByTagText(tagName).isEmpty()) {
			tag = Tag.builder().tagText(tagName).user(user).build();
			tagRepository.save(tag);
		} else {
			tag = tagRepository.findByTagText(tagName).get();
		}
		
		TagToTeam tagToTeam = TagToTeam.from(team, tag);
		tagToTeamRepository.save(tagToTeam);
	}

	public void createTagForProject(Project project, String tagName, Users user) {
		Tag tag;
		if(tagRepository.findByTagText(tagName).isEmpty()) {
			tag = Tag.builder().tagText(tagName).user(user).build();
			tagRepository.save(tag);
		} else {
			tag = tagRepository.findByTagText(tagName).get();
		}
		
		TagToProject tagToProject = TagToProject.from(project, tag);
		tagToProjectRepository.save(tagToProject);
	}
}
