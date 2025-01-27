package com.projet6.payMyBuddy.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.projet6.payMyBuddy.model.Transactions;
import com.projet6.payMyBuddy.model.User;
import com.projet6.payMyBuddy.repository.UserRepository;
import com.projet6.payMyBuddy.service.TransactionService;
import com.projet6.payMyBuddy.service.UserService;

/**
 * Contrôleur pour gérer la navigation entre les différentes pages de
 * l'application PayMyBuddy.
 * <p>
 * Cette classe est responsable de traiter les requêtes HTTP et de retourner des
 * vues correspondant aux différentes pages de l'application. Elle gère les
 * routes principales de l'application, y compris la page d'accueil, la page de
 * connexion, la page d'inscription, la page d'ajout de relation, le profil
 * utilisateur et la page des transactions.
 * </p>
 * 
 * <strong>Routes gérées :</strong>
 * <ul>
 * <li>{@code /} : Page d'accueil (profil utilisateur).</li>
 * <li>{@code /login} : Page de connexion.</li>
 * <li>{@code /signin} : Page d'inscription.</li>
 * <li>{@code /add_relation} : Page pour ajouter une relation.</li>
 * <li>{@code /profil} : Page de profil utilisateur.</li>
 * <li>{@code /transfer} : Page des transactions.</li>
 * </ul>
 * 
 * <strong>Services et repositories utilisés :</strong>
 * <ul>
 * <li>{@link UserService} : Service pour gérer les utilisateurs connectés et
 * leurs connexions.</li>
 * <li>{@link UserRepository} : Repository pour accéder aux données
 * utilisateur.</li>
 * <li>{@link TransactionService} : Service pour gérer les transactions et
 * récupérer les données associées.</li>
 * </ul>
 * 
 * <strong>Gestion des erreurs :</strong>
 * <p>
 * En cas d'erreur, une vue d'erreur est affichée avec un message d'erreur
 * approprié. Par exemple, si la récupération des données échoue sur la page de
 * transfert, une vue "error" est retournée.
 * </p>
 * 
 * <strong>Journalisation :</strong>
 * <p>
 * La classe utilise Log4j2 pour enregistrer les événements importants de
 * l'application, comme les tentatives d'accès aux pages ou les erreurs lors de
 * la récupération des données.
 * </p>
 * 
 * @author [Votre Nom]
 */
@Controller
public class NavigationController {

	private static final Logger logger = LogManager.getLogger(NavigationController.class);

	@Autowired
	public UserService userService;

	@Autowired
	public UserRepository userRepository;

	@Autowired
	private TransactionService transactionService;

	/**
	 * Affiche la page d'accueil de l'application PayMyBuddy.
	 * <p>
	 * Cette méthode gère la route principale de l'application. Elle permet à
	 * l'utilisateur de visualiser son profil si l'utilisateur est connecté.
	 * </p>
	 * 
	 * <strong>Route :</strong> {@code /}
	 * 
	 * @param model Le modèle qui sera envoyé à la vue.
	 * @return La vue de la page du profil.
	 */
	@GetMapping("/")
	public String afficherPageAccueil(Model model) {
		logger.info("Tentative d'accès à la page du profil.");
		return "profil";
	}

	/**
	 * Affiche la page de connexion.
	 * <p>
	 * Cette méthode gère la route pour afficher la page de connexion de
	 * l'utilisateur.
	 * </p>
	 * 
	 * <strong>Route :</strong> {@code /login}
	 * 
	 * @param model Le modèle qui sera envoyé à la vue.
	 * @return La vue de la page de connexion.
	 */
	@GetMapping("/login")
	public String afficherPageLogIn(Model model) {
		logger.info("Tentative d'accès à la page de connexion.");
		return "login";
	}

	/**
	 * Affiche la page d'inscription.
	 * <p>
	 * Cette méthode gère la route pour afficher la page d'inscription de
	 * l'utilisateur.
	 * </p>
	 * 
	 * <strong>Route :</strong> {@code /signin}
	 * 
	 * @param model Le modèle qui sera envoyé à la vue.
	 * @return La vue de la page d'inscription.
	 */
	@GetMapping("/signin")
	public String afficherPageSignIn(Model model) {
		logger.info("Tentative d'accès à la page d'inscription.");
		return "signin";
	}

	/**
	 * Affiche la page pour ajouter une relation.
	 * <p>
	 * Cette méthode gère la route pour afficher la page permettant à l'utilisateur
	 * d'ajouter une relation.
	 * </p>
	 * 
	 * <strong>Route :</strong> {@code /add_relation}
	 * 
	 * @param model Le modèle qui sera envoyé à la vue.
	 * @return La vue de la page d'ajout de relation.
	 */
	@GetMapping("/add_relation")
	public String afficherPageAddRelation(Model model) {
		logger.info("Tentative d'accès à la page d'ajout de relation.");
		return "add_relation";
	}

	/**
	 * Affiche le profil de l'utilisateur connecté.
	 * <p>
	 * Cette méthode gère la route pour afficher le profil de l'utilisateur
	 * connecté. Elle récupère les informations de l'utilisateur via le service
	 * {@link UserService} et les ajoute au modèle. Si l'utilisateur n'est pas
	 * connecté, il est redirigé vers la page de connexion.
	 * </p>
	 * 
	 * <strong>Route :</strong> {@code /profil}
	 * 
	 * @param model Le modèle qui sera envoyé à la vue.
	 * @return La vue de la page de profil, ou une redirection vers la page de
	 *         connexion si l'utilisateur n'est pas connecté.
	 * @throws Exception Si une erreur survient lors de la récupération des
	 *                   informations utilisateur.
	 */
	@GetMapping("/profil")
	public String afficherPageUserProfil(Model model) throws Exception {
		logger.info("Tentative d'accès au profil utilisateur.");
		User userAuth = userService.getCurrentUser();
		System.out.println("le userAuth : " + userAuth);
		if (userAuth == null) {
			logger.debug("L'utilisateur n'a pas pu être identifié. L'utilisateur : {}", userAuth);
			return "redirect:/login";
		} else {
			model.addAttribute("username", userAuth.getUsername());
			model.addAttribute("email", userAuth.getEmail());
			model.addAttribute("password", userAuth.getPassword());
			return "/profil";
		}
	}

	/**
	 * Affiche la page des transactions.
	 * <p>
	 * Cette méthode gère la route pour afficher la page des transactions. Elle
	 * récupère la liste des connexions et des transactions associées via les
	 * services {@link UserService} et {@link TransactionService}, et les ajoute au
	 * modèle pour les afficher sur la page.
	 * </p>
	 * 
	 * <strong>Route :</strong> {@code /transfer}
	 * 
	 * @param model Le modèle qui sera envoyé à la vue.
	 * @return La vue de la page des transactions, ou une vue d'erreur en cas de
	 *         problème lors du chargement des données.
	 */
	@GetMapping("/transfer")
	public String afficherPageTransfer(Model model) {
		logger.info("Tentative d'accès à la page des transactions.");
		try {
			logger.debug("Récupération des connexions et transactions.");
			List<User> connections = userService.getConnections();
			List<Transactions> transactions = transactionService.getAllTransactionById();

			logger.debug("Liste des connexions : {} ", connections);
			logger.debug("Liste des transactions : {} ", transactions);

			model.addAttribute("connections", connections);
			model.addAttribute("transactions", transactions);

			logger.info("Données de transfert récupérées avec succès.");
			return "transfer";
		} catch (Exception e) {
			logger.error("Erreur lors de la récupération des données pour la page de transfert.", e);
			model.addAttribute("error", "Une erreur s'est produite lors du chargement des données.");
			return "error";
		}
	}

}
