package com.creatival;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	@GetMapping("/novel_list")
	public String novel_list() {
		return "novel_list";
	}
	
	@GetMapping("/novel_detail")
	public String novel_detail() {
		return "novel_detail";
	}
	
	@GetMapping("/novel_viewer")
	public String novel_viewer() {
		return "novel_viewer";
	}
	
	@GetMapping("/novel_write")
	public String novel_write() {
		return "novel_write";
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
	
	@GetMapping("/sungmin") 
	public String sungmin() {
		return "index";
	}

	@GetMapping("/teamzang")
	public String teamzang() {
		return "teamzang";
	}
}
