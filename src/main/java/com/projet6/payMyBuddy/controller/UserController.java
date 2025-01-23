package com.projet6.payMyBuddy.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.projet6.payMyBuddy.model.User;
import com.projet6.payMyBuddy.service.UserService;


@Controller
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	private static final Logger logger = LogManager.getLogger(UserController.class);

	@GetMapping("/list_user")
	public ResponseEntity<?> getAllUser() {
		List<User> userList = userService.getAllUser();
		
		if (userList.isEmpty()) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.ok(userList);
		}
	}

	@PostMapping("/add_user")
	public String addUser(@RequestParam String username, @RequestParam String email, @RequestParam String password)
			throws Exception {
		logger.debug("Entrée dans le controller /users/add_user.");
		userService.addUser(username, email, password);
		ResponseEntity.status(HttpStatus.OK);
		return "redirect:/profil";
	}

	@PostMapping("/add_connection")
	public ResponseEntity<String> addConnection(@RequestParam String email) throws Exception {
		try {
			logger.debug("l'email dans le controller : " + email);
			if (email == null || email.isEmpty()) {
				logger.error("Email invalide reçu : {}", email);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("L'email est obligatoire.");
			}
			userService.addConnection(email);
	        return ResponseEntity.status(HttpStatus.CREATED)  // 201 Created
                    .header("Location", "/profil")  // L'URL de redirection
                    .build();
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé avec l'email : " + email);
		}
	}


}
