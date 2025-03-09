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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projet6.payMyBuddy.exception.UserRequestAddInvalidException;
import com.projet6.payMyBuddy.model.User;
import com.projet6.payMyBuddy.service.UserService;

/**
 * Contrôleur pour gérer les utilisateurs dans l'application PayMyBuddy.
 * 
 * <p>
 * Ce contrôleur permet de gérer les utilisateurs, en incluant la récupération
 * de la liste des utilisateurs, l'ajout d'un utilisateur et l'ajout d'une
 * connexion pour un utilisateur.
 * </p>
 * 
 * <strong>Routes gérées :</strong>
 * <ul>
 * <li>{@code /users/list_user} : Récupérer la liste des utilisateurs.</li>
 * <li>{@code /users/add_user} : Ajouter un utilisateur.</li>
 * <li>{@code /users/add_connection} : Ajouter une connexion à un utilisateur en
 * fonction de l'email.</li>
 * </ul>
 * 
 * <strong>Services utilisés :</strong>
 * <ul>
 * <li>{@link UserService} : Service pour gérer les utilisateurs.</li>
 * </ul>
 * 
 * <strong>Gestion des erreurs :</strong>
 * <p>
 * Les erreurs sont gérées au niveau des méthodes pour fournir des réponses
 * appropriées, notamment pour les cas où les données sont invalides ou les
 * utilisateurs non trouvés.
 * </p>
 * 
 * <strong>Journalisation :</strong>
 * <p>
 * Cette classe utilise Log4j2 pour enregistrer les événements importants dans
 * le processus d'ajout d'un utilisateur ou d'une connexion, ainsi que pour la
 * gestion des erreurs.
 * </p>
 */
@Controller
@RequestMapping("/users")
public class UserController {

	private static final Logger logger = LogManager.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	/**
	 * Récupère la liste de tous les utilisateurs.
	 * 
	 * @return La réponse contenant la liste des utilisateurs ou un statut HTTP 204
	 *         (No Content) si la liste est vide.
	 * @throws Exception Si une erreur se produit lors de la récupération des
	 *                   utilisateurs.
	 */
	@GetMapping("/list_user")
	public ResponseEntity<List<User>> getAllUser() {
		logger.info("Entrée dans le controller pour récupérer la liste des utilisateurs.");
		List<User> userList = userService.getAllUser();
		return ResponseEntity.ok(userList); //renvoie un tableau vide si la liste est vide.
	}

	/**
	 * Ajoute un nouvel utilisateur.
	 * 
	 * @param username Le nom d'utilisateur de l'utilisateur à ajouter.
	 * @param email    L'email de l'utilisateur à ajouter.
	 * @param password Le mot de passe de l'utilisateur à ajouter.
	 * @return Une réponse HTTP avec un statut 201 (Created) si l'utilisateur est
	 *         ajouté avec succès.
	 * @throws Exception Si une erreur se produit lors de l'ajout de l'utilisateur.
	 */
	@PostMapping("/add_user")
	public String addUser(@RequestParam String username, 
	                      @RequestParam String email,
	                      @RequestParam String password, 
	                      RedirectAttributes redirectAttributes) {
	    logger.info("Entrée dans le controller pour l'ajout d'un nouvel utilisateur.");
		try {
		    userService.addUser(username, email, password);
		    redirectAttributes.addFlashAttribute("info", "Votre compte a été créé avec succès, veuillez vous connecter.");
		    redirectAttributes.addFlashAttribute("email", email);
		    return "redirect:/login";
		} catch (UserRequestAddInvalidException e) {
	        redirectAttributes.addFlashAttribute("error", e.getMessage());
	        redirectAttributes.addFlashAttribute("username", username);
	        redirectAttributes.addFlashAttribute("email", email);
	        return "redirect:/signin";
	    }
	}
	


	/**
	 * Ajoute une connexion à un utilisateur en fonction de l'email fourni.
	 * 
	 * @param email L'email de l'utilisateur avec lequel établir la connexion.
	 * @return Une réponse HTTP avec un statut 201 (Created) si la connexion est
	 *         ajoutée avec succès, ou un statut 400 (Bad Request) si l'email est
	 *         invalide, ou un statut 404 (Not Found) si l'utilisateur n'est pas
	 *         trouvé.
	 * @throws Exception Si une erreur se produit lors de l'ajout de la connexion.
	 */
	@PostMapping("/add_connection")
	public ResponseEntity<String> addConnection(@RequestParam String email) throws Exception {
		try {
			logger.debug("L'email dans le controller : " + email);
			if (email == null || email.isEmpty()) {
				logger.error("Email invalide reçu : {}", email);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("L'email est obligatoire.");
			}
			userService.addConnection(email);
			return ResponseEntity.status(HttpStatus.CREATED) // 201 Created
					.header("Location", "/profil") // L'URL de redirection
					.build();
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé avec l'email : " + email);
		}
	}
}
