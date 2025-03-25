package com.projet6.payMyBuddy.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projet6.payMyBuddy.exception.InvalidRequestException;
import com.projet6.payMyBuddy.exception.UserNotFoundException;
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
	 */
	@GetMapping("/list_user")
	public ResponseEntity<List<User>> getAllUser() {
		logger.info("Entrée dans le controller pour récupérer la liste des utilisateurs.");
		List<User> userList = userService.getAllUser();
		return ResponseEntity.ok(userList);

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
	    logger.debug("Entrée dans le controller pour l'ajout d'un nouvel utilisateur.");
	    
	    try {
	        userService.addUser(username, email, password);
	        
	        redirectAttributes.addFlashAttribute("info", "Votre compte a été créé avec succès, veuillez vous connecter.");
	        redirectAttributes.addFlashAttribute("email", email);
	        
	        logger.info("Création de l'utilisateur réussie, redirection vers la page login.");
	        return "redirect:/login";
	        
	    } catch (RuntimeException e) {
	        redirectAttributes.addFlashAttribute("error", e.getMessage());
	        redirectAttributes.addFlashAttribute("username", username);
	        redirectAttributes.addFlashAttribute("email", email);
	        
	        logger.error("Requête non valide, redirection vers la page signin.");
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
	public String addConnection(@RequestParam String email, RedirectAttributes redirectAttributes) {
	    logger.debug("L'email dans le controller : " + email);
	    try {
	        userService.addConnection(email);
	        redirectAttributes.addFlashAttribute("info", "La connexion entre utilisateurs a été effectuée avec succès.");
	        return "redirect:/profil";
	    } catch (InvalidRequestException e) {
	        logger.error("InvalidRequestException attrapée : {}", e.getMessage()); // Ajoute ce log
	        redirectAttributes.addFlashAttribute("error", "L'adresse email est invalide.");
	        return "redirect:/add_relation";
	    } catch (UserNotFoundException e) {
	        logger.error("UserNotFoundException attrapée : {}", e.getMessage());
	        redirectAttributes.addFlashAttribute("error", "L'utilisateur à cette adresse mail n'existe pas.");
	        return "redirect:/add_relation";
	    }
	}

}
