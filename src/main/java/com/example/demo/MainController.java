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
	@GetMapping("/teamzang")
	public String teamzang() {
		return "teamzang";
	}
}
