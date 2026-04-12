package com.creatival.bookmark;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.creatival.bookmark.dto.CreateBookmarkDTO;
import com.creatival.bookmark.dto.ResponseBookmarkDTO;
import com.creatival.content.DTO.ResponseContentListForProject;
import com.creatival.like.LikeService;
import com.creatival.like.dto.CreateLikeDTO;
import com.creatival.like.dto.LikeDTO;
import com.creatival.user.UserService;
import com.creatival.user.Users;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/bookmark")
public class BookmarkController {
	private final UserService userService;
	private final BookmarkService bookmarkService;
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/toggle")
	@ResponseBody
	public ResponseBookmarkDTO toggle(Principal principal,@RequestBody CreateBookmarkDTO createBookmarkDTO) {
		Users user = userService.getUserByUsername(principal.getName());
		return new ResponseBookmarkDTO(bookmarkService.toggle(createBookmarkDTO.getType(), createBookmarkDTO.getTargetId(), user), bookmarkService.count(createBookmarkDTO.getType(), createBookmarkDTO.getTargetId()));
	}
	
	@GetMapping("/{id}/bookmarked-contents")
	public ResponseEntity<List<ResponseContentListForProject>> getLikedContents(@PathVariable("id") Long id) {
		List<ResponseContentListForProject> list = bookmarkService.getAllBookmarkedContents(id);
		return ResponseEntity.ok(list);
	}
}
