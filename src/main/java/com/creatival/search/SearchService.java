package com.creatival.search;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.creatival.board.Board;
import com.creatival.board.dto.ResponseBoardListDTO;
import com.creatival.board.repository.BoardRepository;
import com.creatival.content.Content;
import com.creatival.content.ContentFileService;
import com.creatival.content.ContentService;
import com.creatival.content.DTO.ResponseContentListForProject;
import com.creatival.content.Enum.ContentType;
import com.creatival.content.repository.ContentRepository;
import com.creatival.search.dto.SearchResultDTO;
import com.creatival.search.dto.SearchSuggestDTO;
import com.creatival.tag.repository.TagToContentRepository;
import com.creatival.tag.repository.TagToProjectRepository;
import com.creatival.tag.repository.TagToTeamRepository;
import com.creatival.tag.repository.TagToUsersRepository;
import com.creatival.team.Project;
import com.creatival.team.Team;
import com.creatival.team.dto.ResponseProjectListDTO;
import com.creatival.team.dto.ResponseTeamListDTO;
import com.creatival.team.repository.ProjectRepository;
import com.creatival.team.repository.TeamRepository;
import com.creatival.user.UserRepository;
import com.creatival.user.Users;
import com.creatival.user.DTO.ResponseProfile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final ContentRepository contentRepository;
    private final BoardRepository boardRepository;
    
    private final ContentFileService contentFileService;

    private final TagToUsersRepository tagToUserRepository;
    private final TagToTeamRepository tagToTeamRepository;
    private final TagToProjectRepository tagToProjectRepository;
    private final TagToContentRepository tagToContentRepository;
    
    public List<ResponseProfile> searchUsers(String keyword) {
        return userRepository.searchUsers(keyword).stream().map(user -> ResponseProfile.from(user)).toList();
    }

    public List<ResponseTeamListDTO> searchTeams(String keyword) {
        return teamRepository.searchTeams(keyword).stream().map(team -> ResponseTeamListDTO.from(team)).toList();
    }

    public List<ResponseProjectListDTO> searchProjects(String keyword) {
        return projectRepository.searchProjects(keyword).stream().map(project -> ResponseProjectListDTO.from(project)).toList();
    }

    public List<ResponseContentListForProject> searchContents(String keyword) {
    	List<Content> list =contentRepository.searchContents(keyword);
    	List<ResponseContentListForProject> result = new ArrayList<>();
    	for(Content content : list) {
    		if(content.getType()==ContentType.ART) {
    			result.add(ResponseContentListForProject.fromArt(content, contentFileService.getContentFileThumbnail(content).getFileUrl())); 
    		} else {
    			result.add(ResponseContentListForProject.fromNovel(content));
    		}
    	}
        return result;
    }

    public List<ResponseBoardListDTO> searchBoards(String keyword) {
        return boardRepository.searchBoards(keyword).stream().map(board -> ResponseBoardListDTO.from(board)).toList();
    }

    public List<ResponseProfile> searchUsersByTag(String keyword) {
        return tagToUserRepository.searchUsersByTag(keyword).stream().map(user -> ResponseProfile.from(user)).toList();
    }

    public List<ResponseTeamListDTO> searchTeamsByTag(String keyword) {
        return tagToTeamRepository.searchTeamsByTag(keyword).stream().map(team -> ResponseTeamListDTO.from(team)).toList();
    }

    public List<ResponseProjectListDTO> searchProjectsByTag(String keyword) {
        return tagToProjectRepository.searchProjectsByTag(keyword).stream().map(project -> ResponseProjectListDTO.from(project)).toList();
    }

    public List<ResponseContentListForProject> searchContentsByTag(String keyword) {
        List<Content> list = tagToContentRepository.searchContentsByTag(keyword);
        List<ResponseContentListForProject> result = new ArrayList<>();
    	for(Content content : list) {
    		if(content.getType()==ContentType.ART) {
    			result.add(ResponseContentListForProject.fromArt(content, contentFileService.getContentFileThumbnail(content).getFileUrl())); 
    		} else {
    			result.add(ResponseContentListForProject.fromNovel(content));
    		}
    	}
        return result;
    }

}