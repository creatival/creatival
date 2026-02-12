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
	
	@GetMapping("/mypage")
	public String mypage() {
		return "mypage";
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
