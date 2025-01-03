package com.projet6.payMyBuddy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.projet6.payMyBuddy.model.User;
import com.projet6.payMyBuddy.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	public List<User> getAllUser() {
		return userService.getAllUser();
	}
	
	public User addUser(@RequestParam String name, @RequestParam String email) {
		return userService.addUser(name, email);
		
	}

}
