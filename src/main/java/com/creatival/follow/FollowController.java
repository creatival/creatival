package com.creatival.follow;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.creatival.bookmark.dto.ResponseBookmarkDTO;
import com.creatival.follow.dto.CreateFollowDTO;
import com.creatival.follow.dto.ResponseFollowDTO;
import com.creatival.like.dto.CreateLikeDTO;
import com.creatival.like.dto.LikeDTO;
import com.creatival.user.UserService;
import com.creatival.user.Users;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/follow")
public class FollowController {
	private final UserService userService;
	private final FollowService followService;
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/toggle")
	@ResponseBody
	public ResponseFollowDTO toggle(Principal principal,@RequestBody CreateFollowDTO createFollowDTO) {
		Users user = userService.getUserByUsername(principal.getName());
		return new ResponseFollowDTO(followService.toggle(user, createFollowDTO.getType(), createFollowDTO.getTargetId()),followService.count(createFollowDTO.getType(), createFollowDTO.getTargetId()));
	}
}
