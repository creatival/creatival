package com.example.demo;

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
	
	@GetMapping("/novel_episode_write")
	public String novel_episode_write() {
		return "novel_episode_write";
	}
	
	@GetMapping("/novel_write")
	public String novel_write() {
		return "novel_write";
	}
	
	@GetMapping("/mypage_home")
	public String mypage_home() {
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