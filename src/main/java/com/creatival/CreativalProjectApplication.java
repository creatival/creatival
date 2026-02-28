package com.creatival;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CreativalProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreativalProjectApplication.class, args);
	}

}
