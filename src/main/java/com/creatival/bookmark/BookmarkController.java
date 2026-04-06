package com.creatival.bookmark;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.creatival.bookmark.dto.CreateBookmarkDTO;
import com.creatival.bookmark.dto.ResponseBookmarkDTO;
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
}
