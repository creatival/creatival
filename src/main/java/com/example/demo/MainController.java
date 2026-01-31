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
	
<<<<<<< HEAD
	@GetMapping("/seungjae")
	public String seungjae() {
		return "seungjae";
=======
	@GetMapping("/teamzang")
	public String teamzang() {
		return "teamzang";
>>>>>>> branch 'develop' of https://github.com/creatival/creatival.git
	}
}
