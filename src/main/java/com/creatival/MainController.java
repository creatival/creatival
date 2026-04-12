package com.creatival;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.creatival.comment.CommentService;
import com.creatival.content.ContentService;
import com.creatival.tag.TagService;
import com.creatival.team.TeamService;
import com.creatival.team.repository.TeamMemberRepository;
import com.creatival.team.repository.TeamRepository;
import com.creatival.token.UserTokenService;
import com.creatival.user.UserService;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class MainController {
	
	private final TeamService teamService;
	private final ContentService contentService;
	
	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("topProjects", teamService.getTop3Projects());
		model.addAttribute("topArt", contentService.getTopArt(6));
		model.addAttribute("topNovel", contentService.getTopNovel(6));
		model.addAttribute("topVideo", contentService.getTopVideo(6));
		model.addAttribute("topMusic", contentService.getTopMusic(6));
		return "index";
	}
	
	
	
	@GetMapping("/novel_detail")
	public String novel_detail() {
		return "novel_detail";
	}
	
	@GetMapping("/novel_viewer")
	public String novel_viewer() {
		return "novel_viewer";
	}
	
	
	
	@GetMapping("/novel_edit")
	public String novel_edit() {
		return "novel_edit";
	}
	
	@GetMapping("/novel_episode_write")
	public String novel_episode_write() {
		return "novel_episode_write";
	}
	@GetMapping("/novel_episode_write_edit")
	public String novel_episode_write_edit() {
		return "novel_episode_write_edit";
	}
	
	@GetMapping("/comic_list")
	public String comic_list() {
		return "comic_list";
	}
	
	@GetMapping("/comic_detail")
	public String comic_detail() {
		return "comic_detail";
	}
	
	@GetMapping("/comic_write")
	public String comic_write() {
		return "comic_write";
	}
	
	@GetMapping("/comic_viewer")
	public String comic_viewer() {
		return "comic_viewer";
	}
	
	@GetMapping("/Illustration_list")
	public String Illustration_list() {
		return "Illustration_list";
	}
	
	@GetMapping("/Illustration_detail")
	public String Illustration_detail() {
		return "Illustration_detail";
	}
	
	@GetMapping("/Illustration_write")
	public String Illustration_write() {
		return "Illustration_write";
	}
	
	@GetMapping("/mypage")
	public String mypage() {
		return "mypage_home";
	}
	@GetMapping("/user_board")
	public String user_board() {
		return "user_board";
	}
	
	@GetMapping("/board_detail")
	public String board_detail() {
		return "board_detail";
	}
	
	@GetMapping("/board_write")
	public String board_write() {
		return "board_write";
	}
	
	@GetMapping("/originator_board") 
	public String originator_board() {
		return "originator_board";
	}
	
	@GetMapping("/team_list") 
	public String team_list() {
		return "team_list";
	}

	@GetMapping("/team_write")
	public String team_write() {
		return "team_write";
	}
	
	@GetMapping("/team_detail")
	public String team_detail() {
		return "team_detail";
	}
	
	@GetMapping("/team_opinions")
	public String team_opinions() {
		return "team_opinions";
	}
	
	@GetMapping("/team_project_list")
	public String team_project_list() {
		return "team_project_list";
	}
	
	@GetMapping("/team_project_write")
	public String team_project_write() {
		return "team_project_write";
	}
	
	@GetMapping("/team_project_detail")
	public String team_project_detail() {
		return "team_project_detail";
	}
	
	@GetMapping("/team_project_edit")
	public String team_project_edit() {
		return "team_project_edit";
	}
	@GetMapping("/team_project_illust_detail")
	public String team_project_illust_detail() {
		return "team_project_illust_detail";
	}
	
	
	@GetMapping("/team_project_file_upload")
	public String team_project_file_upload() {
		return "team_project_file_upload";
	}
	
	@GetMapping("/video_list")
	public String video_list() {
		return "video_list";
	}
	
	@GetMapping("/video_write")
	public String video_write() {
		return "video_write";
	}
	
	@GetMapping("/video_detail")
	public String video_detail() {
		return "video_detail";
	}
	
	@GetMapping("/music_list")
	public String music_list() {
		return "music_list";
	}
	
	@GetMapping("/music_write")
	public String music_write() {
		return "music_write";
	}
	
	@GetMapping("/music_detail")
	public String music_detail() {
		return "music_detail";
	}
	
	@GetMapping("/file_list")
	public String file_list() {
		return "file_list";
	}
	
	@GetMapping("/file_detail")
	public String file_detail() {
		return "file_detail";
	}
	
	@GetMapping("/file_write")
	public String file_write() {
		return "file_write";
	}
}
