package com.projet6.payMyBuddy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.projet6.payMyBuddy.model.Transactions;
import com.projet6.payMyBuddy.model.User;
import com.projet6.payMyBuddy.service.TransactionService;
import com.projet6.payMyBuddy.service.UserService;

@Controller
public class NavigationController {
	
	@Autowired
	public UserService userService;
	
	@Autowired
	private TransactionService transactionService;
	
	@GetMapping("/")
	public String afficherPageAccueil(Model model) {
		return "profil";
	}

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
		User currentUser = userService.getCurrentUser();
		String username = currentUser.getUsername();
		String email = currentUser.getEmail();
		String mdp = currentUser.getPassword();
		model.addAttribute("username", username);
		model.addAttribute("email", email);
		model.addAttribute("password", mdp);
        return "profil";
    }
	
	@GetMapping("/transfer")
	public String afficherPageTransfer(Model model) throws Exception {
        List<User> connections = userService.getConnections();
        List<Transactions> transactions = transactionService.getAllTransactionById();
        
        model.addAttribute("connections", connections);
        model.addAttribute("transactions", transactions);
        
        return "transfer"; 
	}

}
