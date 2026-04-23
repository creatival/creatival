package com.creatival.like;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.creatival.board.BoardService;
import com.creatival.content.Content;
import com.creatival.content.ContentFileService;
import com.creatival.content.ContentService;
import com.creatival.content.DTO.ResponseContentListForProject;
import com.creatival.content.Enum.ContentType;
import com.creatival.like.dto.LikeDTO;
import com.creatival.like.repository.LikeRepository;
import com.creatival.team.TeamService;
import com.creatival.user.UserService;
import com.creatival.user.Users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class LikeService {
	
	private final ContentService contentService;
	private final BoardService boardService;
	private final TeamService teamService;
	private final LikeRepository likeRepository;
	private final UserService userService;
	private final ContentFileService contentFileService;

	@Transactional
	public boolean toggle(TargetType type, Long targetId, Users user) {
		System.out.println("targetType = " + type);
		System.out.println("targetId = " + targetId);
		switch (type) {
			case CONTENT : {
				if(contentService.getContent(targetId)==null) {
					throw new IllegalArgumentException("정보가 잘 못 되었습니다.");
				}
				break;
			}
			case BOARD : {
				if(boardService.getBoardById(targetId)==null) {
					throw new IllegalArgumentException("정보가 잘 못 되었습니다.");
				}
				break;
			}
			case TEAM : {
				if(teamService.getTeamById(targetId)==null) {
					throw new IllegalArgumentException("정보가 잘 못 되었습니다.");
				}
				break;
			}
			case PROJECT : {
				if(teamService.getProjectById(targetId)==null) {
					throw new IllegalArgumentException("정보가 잘 못 되었습니다.");
				}
				break;
			}
			case EPISODE : {
				if(contentService.getEpisodeById(targetId)==null) {
					throw new IllegalArgumentException("정보가 잘 못 되었습니다.");
				}
				break;
			}
			default : {
				throw new IllegalArgumentException("정보가 잘 못 되었습니다.");
			}
		}
		Optional<Likes> optional = likeRepository.findByUserAndTargetTypeAndTargetId(user, type, targetId);
		if(optional.isPresent()) {
			likeRepository.delete(optional.get());
			downLike(targetId, type);
			return false;
		} else {
			Likes like = Likes.builder()
					.user(user)
					.targetId(targetId)
					.targetType(type)
					.build();
			likeRepository.save(like);
			upLike(targetId, type);
			return true;
		}
	}

	private void upLike(Long targetId, TargetType type) {
		switch (type) {
			case CONTENT : {
				if(contentService.getContent(targetId)==null) {
					throw new IllegalArgumentException("정보가 잘 못 되었습니다.");
				}
				contentService.upLikeCount(targetId);
				break;
			}
			case BOARD : {
				if(boardService.getBoardById(targetId)==null) {
					throw new IllegalArgumentException("정보가 잘 못 되었습니다.");
				}
				break;
			}
			case TEAM : {
				if(teamService.getTeamById(targetId)==null) {
					throw new IllegalArgumentException("정보가 잘 못 되었습니다.");
				}
				teamService.upLikeCount(targetId);
				break;
			}
			case PROJECT : {
				if(teamService.getProjectById(targetId)==null) {
					throw new IllegalArgumentException("정보가 잘 못 되었습니다.");
				}
				teamService.upLikeCountForProject(targetId);
				break;
			}
			case EPISODE : {
				if(contentService.getEpisodeById(targetId)==null) {
					throw new IllegalArgumentException("정보가 잘 못 되었습니다.");
				}
				break;
			}
			default : {
				throw new IllegalArgumentException("정보가 잘 못 되었습니다.");
			}
		}
	}

	private void downLike(Long targetId, TargetType type) {
		switch (type) {
			case CONTENT : {
				if(contentService.getContent(targetId)==null) {
					throw new IllegalArgumentException("정보가 잘 못 되었습니다.");
				}
				contentService.downLikeCount(targetId);
				break;
			}
			case BOARD : {
				if(boardService.getBoardById(targetId)==null) {
					throw new IllegalArgumentException("정보가 잘 못 되었습니다.");
				}
				break;
			}
			case TEAM : {
				if(teamService.getTeamById(targetId)==null) {
					throw new IllegalArgumentException("정보가 잘 못 되었습니다.");
				}
				teamService.downLikeCount(targetId);
				break;
			}
			case PROJECT : {
				if(teamService.getProjectById(targetId)==null) {
					throw new IllegalArgumentException("정보가 잘 못 되었습니다.");
				}
				break;
			}
			case EPISODE : {
				if(contentService.getEpisodeById(targetId)==null) {
					throw new IllegalArgumentException("정보가 잘 못 되었습니다.");
				}
				break;
			}
			default : {
				throw new IllegalArgumentException("정보가 잘 못 되었습니다.");
			}
		}
		
	}

	public Long count(TargetType type, Long targetId) {
		return likeRepository.countByTargetIdAndTargetType(targetId, type);
	}
	
	public boolean isLiked(Users user, TargetType type, Long targetId) {
		Optional<Likes> optional = likeRepository.findByUserAndTargetTypeAndTargetId(user, type, targetId);
		if(optional.isPresent()) {
			return true;
		} else {
			return false;
		}
	}
	
	public LikeDTO getLike(Users user, TargetType type, Long targetId) {
		if(user == null) {
			long count = likeRepository.countByTargetIdAndTargetType(targetId, type);
			return LikeDTO.from(false, count);
		}
		Optional<Likes> optional = likeRepository.findByUserAndTargetTypeAndTargetId(user, type, targetId);
		if(optional.isPresent()) {
			long count = likeRepository.countByTargetIdAndTargetType(targetId, type);
			return LikeDTO.from(true, count);
		} else {
			long count = likeRepository.countByTargetIdAndTargetType(targetId, type);
			return LikeDTO.from(false, count);
		}
	}
	public List<Likes> getLikeContents(Users user) {
		List<Likes> list = likeRepository.findByUserAndTargetType(user, TargetType.CONTENT);
		return list;
	}
	public List<ResponseContentListForProject> myPageLikePreview(Users user, TargetType targetType) {
		List<Likes> list = likeRepository.findTop6ByUserAndTargetTypeOrderByCreatedAt(user, targetType);
		List<ResponseContentListForProject> contentList = new ArrayList<>();
		for(Likes like : list) {
			Content likeContent = contentService.getContent(like.getTargetId());
			if(likeContent.getType()==ContentType.ART) {
				contentList.add(ResponseContentListForProject.fromArt(likeContent, contentFileService.getContentFileThumbnail(likeContent).getFileUrl()));
			} else {
				contentList.add(ResponseContentListForProject.fromNovel(likeContent));
			}
		}
		return (contentList != null) ? contentList : new ArrayList<>();
	}
	//contentLike는 Likes임
	public List<ResponseContentListForProject> getAllLikedContents(Long userId) {
		List<ResponseContentListForProject> allLikeContentList = new ArrayList<>();
		List<Likes> likeContentList = getLikeContents(userService.getUserById(userId));
		for (Likes contentLike : likeContentList) {
			Content content = contentService.getContent(contentLike.getTargetId());
			if(content.getType()==ContentType.ART) {
				allLikeContentList.add(ResponseContentListForProject.fromArt(content, contentFileService.getContentFileThumbnail(content).getFileUrl()));
			} else {
				allLikeContentList.add(ResponseContentListForProject.fromNovel(content));
			}
		}
		return allLikeContentList;
		
	}

	public void deleteLike(TargetType content, Long id) {
		likeRepository.deleteByTargetIdAndTargetType(id, content);
		
	}
	
}
