package com.projet6.payMyBuddy.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.projet6.payMyBuddy.model.User;
import com.projet6.payMyBuddy.repository.UserRepository;

@Service
public class UserService {

	private static final Logger logger = LogManager.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepository;

	public User getCurrentUser() throws Exception {
		logger.info("Entrée dans la méthode UserService.getcurrentUser().");
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			logger.debug("L'authentification : {} ", authentication);
			Object principal = authentication.getPrincipal();
			logger.debug("L'object principal : {} ", principal);

			if (principal instanceof OAuth2User) { // Vérifiez que l'utilisateur est authentifié via OAuth2
				User oAuthUser = getUserWithOauth((OAuth2User) principal); // Cast explicite
				return oAuthUser;
 
			} else if (principal instanceof UserDetails) {
				logger.debug("Utilisateur authentifié sur le site.");
				User currentUser = getUserWithUserDetails();
				logger.debug("Le currentUser : {} ", currentUser);

				if (currentUser != null) {
					logger.info("Utilisateur trouvé : {}", currentUser.getUsername());
					return currentUser;
				} else {
					logger.error("Utilisateur non trouvé dans la base de données.");
					return null;
				}
			}
		} catch (Exception e) {
			throw new Exception("Une erreur s'est produite.");
		}
		return null;
	}

	public User getUserWithUserDetails() {
		logger.info("Entrée dans la méthode userService.getUserWithUserDetails.");
		try {
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

			if (principal instanceof UserDetails) {
				String email = ((UserDetails) principal).getUsername();
				logger.debug("L'email : {} ", email);
				return userRepository.findByEmail(email);
			} else {
				logger.warn("Le principal n'est pas une instance de UserDetails : {}", principal);
				return null;
			}
		} catch (Exception e) {
			logger.error("Erreur lors de la récupération de l'utilisateur actuel", e);
			return null;
		}
	}

	public User getUserWithOauth(OAuth2User oAuth2User) throws Exception {
		logger.info("Entrée dans la méthode UserService.getUserWithOAuth().");
		try {
			String username = oAuth2User.getAttribute("login");
			String email = oAuth2User.getAttribute("email");

			logger.debug("L'utilisateur se connecte avec pour email : {} et pour username : {} ", email, username);

			User oAuthUser = userRepository.findByEmail(email);

			if (oAuthUser == null) {
				logger.debug("Utilisateur non trouvé, création d'un nouvel utilisateur.");

				String password = "test";
				User newUser = addUser(username, email, password);

				return newUser;
			}
			logger.debug("Utilisateur trouvé : {}", oAuthUser.getUsername());
			return oAuthUser;
		} catch (Exception e) {
			logger.error("Une erreur est survenue dans la récupération de l'utilisateur.");
			throw new Exception("Une erreur est survenue dans la récupération de l'utilisateur." + e);
		}
	}

	public List<User> getAllUser() throws Exception {
		logger.info("Entreé dans la méthode userService.getAllUser().");
		try {
			List<User> userList = userRepository.findAll();
			logger.debug("La liste des utilisateurs : {} ", userList);
			return userList;
		} catch (Exception e) {
			logger.error("une erreur est survenue lors de la récupération de la liste des utilisateurs.");
			throw new Exception("une erreur est survenue lors de la récupération de la liste des utilisateurs." + e);
		}

	}

	public void addConnection(String email) throws Exception {
		try {
			logger.debug("l'email dans le service = " + email);
			User actualUser = getCurrentUser();
			User userToAdd = userRepository.findByEmail(email);
			logger.info("l actualUser = " + actualUser);
			logger.info("le userToAdd = " + userToAdd);

			if (userToAdd == null) {
				logger.error("utilisateur non trouvé avec l'email : " + email);
				throw new RuntimeException("Utilisateur non trouvé avec l'email : " + email);
			}
			if (!actualUser.getConnections().contains(userToAdd)) {
				actualUser.getConnections().add(userToAdd);
			}
			if (!userToAdd.getConnections().contains(actualUser)) {
				userToAdd.getConnections().add(actualUser);
			}

			userRepository.save(actualUser);
			userRepository.save(userToAdd);
		} catch (Exception e) {
			logger.error("Une erreur s'est produite lors de l'ajout d'une nouvelle relation pour l'email : " + email);
			throw new Exception(
					"Une erreur s'est produite lors de l'ajout d'une nouvelle relation pour l'email : " + email);
		}

	}

	public User addUser(String username, String email, String password) throws Exception {
		logger.debug("Entrée dans la méthode addUser de la class UserService");
		try {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			String encodedPassword = encoder.encode(password);

			User newUser = new User();
			newUser.setUsername(username);
			newUser.setEmail(email);
			newUser.setPassword(encodedPassword);
			newUser.setRole("user");

			logger.debug("le nouvel utilisateur : {}" + newUser);

			return userRepository.save(newUser);
		} catch (Exception e) {
			throw new Exception("Un probleme a eu lieu lors de l ajout d'un nouvel utilisateur : {}", e);
		}

	}

	public List<User> getConnections() throws Exception {
		logger.info("Entrée dans la méthode UserService.getConnections().");
		User currentUser = null;
		try {
			currentUser = getCurrentUser();
			logger.debug("Voici le currentUser : {} ", currentUser);
			if (currentUser == null) {
				logger.error("Le currentUser est null.");
				throw new Exception("Le currentUser est null.");
			}
			return currentUser.getConnections();
		} catch (Exception e) {
			logger.error(
					"Une erreur est survenue lors de la récupération des connections pour l'utilisateur : {}. Détails : {}",
					currentUser, e);
			throw new Exception("Une erreur est survenue lors de la récupération des connections." + e);
		}
	}

}
