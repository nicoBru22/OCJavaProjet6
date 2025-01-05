package com.projet6.payMyBuddy.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.projet6.payMyBuddy.model.User;
import com.projet6.payMyBuddy.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	public List<User> getAllUser() {
		return userRepository.findAll();
	}
	
	public User addUser(String username, String email, String password) throws Exception {
		try {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			String encodedPassword = encoder.encode(password);
			
			User newUser = new User();
			newUser.setUsername(username);
			newUser.setEmail(email);
			newUser.setPassword(encodedPassword);
			newUser.setRole("user");
			
			System.out.println("le nouvel utilisateur : {}"+ newUser);
			
			return userRepository.save(newUser);
		} catch (Exception e) {
			throw new Exception("Un probleme a eu lieu lors de l ajout d'un nouvel utilisateur : {}", e);
		}

	}

}
