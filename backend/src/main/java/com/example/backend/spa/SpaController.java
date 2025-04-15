package com.example.backend.spa;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {

	@RequestMapping({"/", "/login", "/register", "/user-home/**", "/401", "/403", "/404"})
	public String redirect() {
		return "forward:/index.html";
	}
}
