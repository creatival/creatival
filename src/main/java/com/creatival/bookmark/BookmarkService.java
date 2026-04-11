package com.creatival.bookmark;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.creatival.board.BoardService;
import com.creatival.bookmark.dto.ResponseBookmarkDTO;
import com.creatival.bookmark.repository.BookmarkRepository;
import com.creatival.content.ContentService;
import com.creatival.like.Likes;
import com.creatival.like.TargetType;
import com.creatival.like.dto.LikeDTO;
import com.creatival.like.repository.LikeRepository;
import com.creatival.team.TeamService;
import com.creatival.user.Users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class BookmarkService {

	private final ContentService contentService;
	private final BoardService boardService;
	private final TeamService teamService;
	private final BookmarkRepository bookmarkRepository;

	@Transactional
	public boolean toggle(TargetType type, Long targetId, Users user) {
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
		Optional<Bookmark> optional = bookmarkRepository.findByUserAndTargetTypeAndTargetId(user, type, targetId);
		if(optional.isPresent()) {
			bookmarkRepository.delete(optional.get());
			return false;
		} else {
			Bookmark bookmark = Bookmark.builder()
					.user(user)
					.targetId(targetId)
					.targetType(type)
					.build();
			bookmarkRepository.save(bookmark);
			return true;
		}
	}
	public Long count(TargetType type, Long targetId) {
		return bookmarkRepository.countByTargetIdAndTargetType(targetId, type);
	}
	
	public ResponseBookmarkDTO getBookmark(Users user, TargetType type, Long targetId) {
		if(user == null) {
			long count = bookmarkRepository.countByTargetIdAndTargetType(targetId, type);
			return ResponseBookmarkDTO.from(false, count);
		}
		Optional<Bookmark> optional = bookmarkRepository.findByUserAndTargetTypeAndTargetId(user, type, targetId);
		if(optional.isPresent()) {
			long count = bookmarkRepository.countByTargetIdAndTargetType(targetId, type);
			return ResponseBookmarkDTO.from(true, count);
		} else {
			long count = bookmarkRepository.countByTargetIdAndTargetType(targetId, type);
			return ResponseBookmarkDTO.from(false, count);
		}
	}

}
