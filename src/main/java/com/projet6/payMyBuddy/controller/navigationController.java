package com.projet6.payMyBuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class navigationController {

	@GetMapping("/login")
	public String afficherPageLogIn(Model model) {;
		return "login";
	}

	@GetMapping("/signin")
	public String afficherPageSignIn(Model model) {
		return "signin";
	}
	
	@GetMapping("/add_relation")
	public String afficherPageAddRelation(Model model) {
		return "add_relation";
	}
	
	@GetMapping("/profil")
	public String afficherPageUserProfil(Model model) {
		return "profil";
	}
	
	@GetMapping("/transfer")
	public String afficherPageTransfer(Model model) {
		return "transfer";
	}

}
