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

import com.projet6.payMyBuddy.exception.InvalidRequestException;
import com.projet6.payMyBuddy.exception.UserNotFoundException;
import com.projet6.payMyBuddy.exception.UserRequestAddInvalidException;
import com.projet6.payMyBuddy.model.User;
import com.projet6.payMyBuddy.repository.UserRepository;

/**
 * Service pour gérer les utilisateurs. Fournit des méthodes pour récupérer,
 * ajouter et gérer les utilisateurs.
 */
@Service
public class UserService {

	private static final Logger logger = LogManager.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepository;

	/**
	 * Récupère l'utilisateur actuellement authentifié, en prenant en charge
	 * l'authentification OAuth2 ou classique.
	 * 
	 * <p>
	 * Cette méthode utilise le contexte de sécurité pour accéder à
	 * l'authentification de l'utilisateur connecté. Si l'utilisateur est
	 * authentifié via OAuth2, ses informations sont récupérées à l'aide de la
	 * méthode {@link #getUserWithOauth(OAuth2User)}. Pour une authentification
	 * classique, les détails d'utilisateur sont traités via
	 * {@link #getUserWithUserDetails()}.
	 * </p>
	 * 
	 * @return un objet {@link User} représentant l'utilisateur actuellement
	 *         authentifié, ou {@code null} si l'utilisateur ne peut pas être
	 *         déterminé
	 * @throws Exception si une erreur survient lors de la récupération de
	 *                   l'utilisateur
	 */

	public User getCurrentUser(){
		logger.info("Entrée dans la méthode UserService.getcurrentUser().");
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

			if (currentUser == null) {
				logger.error("Utilisateur non trouvé dans la base de données : {} ", currentUser);
				throw new UserNotFoundException("L'utilisateur n'existe pas en base de donnée : "+ currentUser);
			}
			
			logger.info("Utilisateur trouvé : {}", currentUser.getUsername());
			return currentUser;

		}
		return null;
	}

	/**
	 * Récupère l'utilisateur actuellement authentifié via les détails
	 * d'authentification du contexte de sécurité.
	 * 
	 * <p>
	 * Cette méthode accède au contexte de sécurité pour obtenir l'objet principal
	 * de l'utilisateur connecté. Si le principal est une instance de
	 * {@link UserDetails}, l'email est extrait et utilisé pour récupérer
	 * l'utilisateur correspondant depuis le repository. Si le principal n'est pas
	 * une instance de {@link UserDetails}, un avertissement est enregistré et la
	 * méthode retourne {@code null}.
	 * </p>
	 * 
	 * @return un objet {@link User} représentant l'utilisateur actuellement
	 *         authentifié, ou {@code null} si l'utilisateur ne peut pas être
	 *         déterminé
	 */

	public User getUserWithUserDetails() {
		logger.info("Entrée dans la méthode userService.getUserWithUserDetails.");
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

			if (principal instanceof UserDetails) {
				String email = ((UserDetails) principal).getUsername();
				logger.debug("L'email : {} ", email);
				return userRepository.findByEmail(email);
			} else {
				logger.warn("Le principal n'est pas une instance de UserDetails : {}", principal);
		        throw new UserNotFoundException("Le principal ne correspond pas à un utilisateur valide.");			
	        }
	}

	/**
	 * Récupère ou crée un utilisateur en fonction des données fournies par OAuth2.
	 * 
	 * <p>
	 * Cette méthode tente de récupérer un utilisateur à partir de son adresse email
	 * via le repository. Si aucun utilisateur correspondant n'est trouvé, un nouvel
	 * utilisateur est créé et ajouté au système. La méthode gère également les
	 * exceptions en cas de problème lors de l'exécution.
	 * </p>
	 * 
	 * @param oAuth2User l'utilisateur OAuth2 contenant les informations de
	 *                   connexion telles que le login et l'email
	 * @return un objet {@link User} représentant l'utilisateur récupéré ou créé
	 * @throws Exception si une erreur se produit lors de la récupération ou de la
	 *                   création de l'utilisateur
	 */

	public User getUserWithOauth(OAuth2User oAuth2User) {
		logger.info("Entrée dans la méthode UserService.getUserWithOAuth().");
		
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
	}

	/**
	 * Récupère la liste de tous les utilisateurs.
	 * 
	 * <p>
	 * La méthode récupère la liste contenant tous les utilisateurs et retourne
	 * cette liste.
	 * </p>
	 * 
	 * @return la liste des utilisateurs.
	 * @throws Exception si une erreur survient lors de la récupération de la liste
	 *                   des utilisateurs.
	 */
	public List<User> getAllUser() {
	    logger.info("Entrée dans la méthode userService.getAllUser().");
	    
	    List<User> userList = userRepository.findAll();
	    if (userList.isEmpty()) {
	        logger.warn("Aucun utilisateur trouvé.");
	        throw new UserNotFoundException("Aucun utilisateur trouvé.");
	    }
	    
	    logger.debug("La liste des utilisateurs : {}", userList);
	    return userList;
	}


	/**
	 * Ajoute une nouvel connexion entre utilisateur.
	 * 
	 * <p>
	 * La méthode récupère l'adresse email de l'utilisateur à ajouter en paramètre.
	 * Elle récupère les données sur l'utilisateur actuel puis celle de
	 * l'utilisateur à qui appartient l'adresse email. Si l'utilisateur à ajouter
	 * est nul alors elle lève une exception sinon elle sauvegarde la connexion du
	 * coté des 2 utilisateurs.
	 * </p>
	 * 
	 * @param email l'email de l'utilisateur à ajouter.
	 * @throws Exception si un utilisateur n'est pas trouvé via l'email ou si une
	 *                   erreur se produit pendant l'ajout.
	 */
	public void addConnection(String email) {
	    logger.debug("L'email dans le service = {}", email);
	    
		if (email == null || email.isEmpty()) {
			logger.error("Email invalide reçu : {}", email);
			throw new InvalidRequestException("L'email est vide ou nul : "+ email);
		}
	    
	    User actualUser = getCurrentUser();
	    User userToAdd = userRepository.findByEmail(email);

	    logger.info("ActualUser = {}", actualUser);
	    logger.info("UserToAdd = {}", userToAdd);

	    if (userToAdd == null) {
	        logger.error("Utilisateur non trouvé avec l'email : {}", email);
	        throw new UserNotFoundException("Utilisateur non trouvé avec l'email : " + email);
	    }
	    
	    if(actualUser.getConnections().contains(userToAdd)) {
	    	logger.warn("Une connexion existe déjà entre ces 2 utilisateurs.");
	    	return;
	    }

	    if (!actualUser.getConnections().contains(userToAdd)) {
	        actualUser.getConnections().add(userToAdd);
		    userRepository.save(actualUser);
	    }
	    
	    if (!userToAdd.getConnections().contains(actualUser)) {
	        userToAdd.getConnections().add(actualUser);
		    userRepository.save(userToAdd);
	    }
	    
	    logger.info("Les utilisateurs ont été connectés avec succès.");
	    return;
	}


	/**
	 * Ajoute un nouvel utilisateur à la base de donnée.
	 * 
	 * <p>
	 * La méthode récupère le nom, l'email et le mot de passe dans les paramètres.
	 * Ensuite le mot de passe est hashé. Pour finir, elle créé un nouvel
	 * utilisateur avec l'ensemble des éléments et retoune la sauvegarde du nouvel
	 * utilisateur.
	 * </p>
	 * 
	 * @param username le nom du nouvel utilisateur
	 * @param email    l'email du nouvel utilisateur.
	 * @param password le mot de passe du nouvel utilisateur.
	 * @return la sauvegarde du nouvel utilisateur.
	 * @throws Exception si un problème a eu lieu lors de l'ajout du nouvel
	 *                   utilisateur.
	 */
	public User addUser(String username, String email, String password){
		logger.debug("Entrée dans la méthode addUser de la class UserService");
		
	    if (username == null || username.isEmpty() || 
		        email == null || email.isEmpty() || 
		        password == null || password.isEmpty()) {
	    	logger.error("Tous les champs sont requis. Username : {}, Email : {}, Password : {}", username, email, password);
	    	throw new UserRequestAddInvalidException("Tous les champs sont requis.");
	    }
	    
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodedPassword = encoder.encode(password);

		User newUser = new User();
		newUser.setUsername(username);
		newUser.setEmail(email);
		newUser.setPassword(encodedPassword);
		newUser.setSolde(50);
		newUser.setRole("user");

		logger.debug("le nouvel utilisateur : {}" + newUser);
		
		return userRepository.save(newUser);

	}

	/**
	 * Récupère la liste des connexions aux autres utilisateur.
	 * 
	 * <p>
	 * La méthode récupère les données de l'utilisateur connecté actuellement. Si
	 * l'utilisateur est null alors une exception est levée. Si l'utilisateur n'est
	 * pas null, alors la méthode retourne la liste de connexion.
	 * </p>
	 * 
	 * @return une liste des connexions à d'autres utilisateurs, à partir de
	 *         l'utilisateur connecté.
	 * @throws Exception une exception est levée si une erreur survient lors de la
	 *                   récupération des connexions ou si l'utilisateur est null.
	 */
	public List<User> getConnections() {
		logger.info("Entrée dans la méthode UserService.getConnections().");
		
		User currentUser = null;
		currentUser = getCurrentUser();
		
		logger.debug("Voici le currentUser : {} ", currentUser);
		
		if (currentUser == null) {
			logger.error("Le currentUser est null : {}"+ currentUser);
			throw new UserNotFoundException("Le currentUser est null." + currentUser);
		}
		
		return currentUser.getConnections();
	}

}
