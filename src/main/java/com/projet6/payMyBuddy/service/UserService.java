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
	public User addUser(String username, String email, String password) throws Exception {
		logger.debug("Entrée dans la méthode addUser de la class UserService");
		try {
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
		} catch (Exception e) {
			throw new Exception("Un probleme a eu lieu lors de l ajout d'un nouvel utilisateur : {}", e);
		}

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
