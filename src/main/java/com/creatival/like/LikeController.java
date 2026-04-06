package com.creatival.like;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.creatival.MailService;
import com.creatival.content.ContentService;
import com.creatival.like.dto.CreateLikeDTO;
import com.creatival.like.dto.LikeDTO;
import com.creatival.tag.TagService;
import com.creatival.team.TeamService;
import com.creatival.team.repository.TeamMemberRepository;
import com.creatival.team.repository.TeamRepository;
import com.creatival.token.UserTokenService;
import com.creatival.user.UserService;
import com.creatival.user.Users;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/like")
public class LikeController {
	private final LikeService likeService;
	private final UserService userService;
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/toggle")
	@ResponseBody
	public LikeDTO toggle(Principal principal,@RequestBody CreateLikeDTO createLikeDTO) {
		Users user = userService.getUserByUsername(principal.getName());
		return new LikeDTO(likeService.toggle(createLikeDTO.getType(), createLikeDTO.getTargetId(), user), likeService.count(createLikeDTO.getType(), createLikeDTO.getTargetId()));
	}
}
