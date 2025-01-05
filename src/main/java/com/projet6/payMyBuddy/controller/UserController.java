package com.projet6.payMyBuddy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.projet6.payMyBuddy.model.User;
import com.projet6.payMyBuddy.service.UserService;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/users")
	public List<User> getAllUser() {
		return userService.getAllUser();
	}
	
    @PostMapping("/users")
    public String addUser(@RequestParam String username, 
                          @RequestParam String email, 
                          @RequestParam String password) throws Exception {

        userService.addUser(username, email, password);
        
        return "redirect:/profil";
    }

}
