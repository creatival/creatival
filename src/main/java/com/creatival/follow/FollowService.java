package com.creatival.follow;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.creatival.board.BoardService;
import com.creatival.bookmark.Bookmark;
import com.creatival.bookmark.dto.ResponseBookmarkDTO;
import com.creatival.content.ContentService;
import com.creatival.follow.dto.ResponseFollowDTO;
import com.creatival.follow.repository.FollowRepository;
import com.creatival.like.Likes;
import com.creatival.like.TargetType;
import com.creatival.like.repository.LikeRepository;
import com.creatival.team.TeamService;
import com.creatival.user.UserService;
import com.creatival.user.Users;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FollowService {
	private final TeamService teamService;
	private final UserService userService;
	private final FollowRepository followRepository;

	public boolean toggle(Users user, TargetType type, Long targetId) {
		switch (type) {
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
			case USER : {
				if(userService.getUserById(targetId)==null) {
					throw new IllegalArgumentException("정보가 잘 못 되었습니다.");
				}
				break;
			}
			default : {
				throw new IllegalArgumentException("정보가 잘 못 되었습니다.");
			}
		}
		Optional<Follow> optional = followRepository.findByUserAndTargetTypeAndTargetId(user, type, targetId);
		if(optional.isPresent()) {
			followRepository.delete(optional.get());
			return false;
		} else {
			Follow follow = Follow.builder()
					.user(user)
					.targetId(targetId)
					.targetType(type)
					.build();
			followRepository.save(follow);
			return true;
		}
	}
	public Long count(TargetType type, Long targetId) {
		return followRepository.countByTargetIdAndTargetType(targetId, type);
	}
	
	public ResponseFollowDTO getFollow(Users user, TargetType type, Long targetId) {
		if(user == null) {
			long count = followRepository.countByTargetIdAndTargetType(targetId, type);
			return ResponseFollowDTO.from(false, count);
		}
		Optional<Follow> optional = followRepository.findByUserAndTargetTypeAndTargetId(user, type, targetId);
		if(optional.isPresent()) {
			long count = followRepository.countByTargetIdAndTargetType(targetId, type);
			return ResponseFollowDTO.from(true, count);
		} else {
			long count = followRepository.countByTargetIdAndTargetType(targetId, type);
			return ResponseFollowDTO.from(false, count);
		}
	}

}
