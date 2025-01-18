package com.projet6.payMyBuddy.service;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.projet6.payMyBuddy.model.User;
import com.projet6.payMyBuddy.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	private static final Logger logger = LogManager.getLogger(UserService.class);

	public User getCurrentUser() {
	    try {
	        // Récupération des détails de l'utilisateur connecté
	        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

	        // Vérification si le principal est une instance de UserDetails (utilisateur classique)
	        if (principal instanceof UserDetails) {
	            String email = ((UserDetails) principal).getUsername();
	            return userRepository.findByEmail(email);
	        }
	        // Vérification si le principal est une instance de OAuth2User (utilisateur OAuth2)
	        else if (principal instanceof OAuth2User) {
	            // Dans ce cas, tu peux récupérer l'email depuis les attributs de l'OAuth2User
	            String email = (String) ((OAuth2User) principal).getAttributes().get("email");
	            return userRepository.findByEmail(email);
	        } else {
	            logger.warn("Le principal n'est pas une instance de UserDetails ou OAuth2User : {}", principal);
	            return null;
	        }
	    } catch (Exception e) {
	        logger.error("Erreur lors de la récupération de l'utilisateur actuel", e);
	        return null;
	    }
	}


	public List<User> getAllUser() {
		return userRepository.findAll();
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

			System.out.println("le nouvel utilisateur : {}" + newUser);

			return userRepository.save(newUser);
		} catch (Exception e) {
			throw new Exception("Un probleme a eu lieu lors de l ajout d'un nouvel utilisateur : {}", e);
		}

	}

	public List<User> getConnections() throws Exception {
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
