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

@Controller
public class NavigationController {

    private static final Logger logger = LogManager.getLogger(NavigationController.class);

    @Autowired
    public UserService userService;
    
    @Autowired
    public UserRepository userRepository;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/")
    public String afficherPageAccueil(Model model) {
        logger.info("Tentative d'accès à la page du profil.");
        return "profil";
    }

    @GetMapping("/login")
    public String afficherPageLogIn(Model model) {
        logger.info("Tentative d'accès à la page de connexion.");
        return "login";
    }

    @GetMapping("/signin")
    public String afficherPageSignIn(Model model) {
        logger.info("Tentative d'accès à la page d'inscription.");
        return "signin";
    }

    @GetMapping("/add_relation")
    public String afficherPageAddRelation(Model model) {
        logger.info("Tentative d'accès à la page d'ajout de relation.");
        return "add_relation";
    }

    @GetMapping("/profil")
    public String afficherPageUserProfil(Model model) throws Exception {
        logger.info("Tentative d'accès au profil utilisateur.");
        User userAuth = userService.getCurrentUser();
        System.out.println("le userAuth : " + userAuth);
        if (userAuth == null) {
        	logger.debug("l'utilisateur n'a pas pu être identifier. L'utilisateur : {}", userAuth);
        	return "redirect:/login";
        } else {
            model.addAttribute("username", userAuth.getUsername());
            model.addAttribute("email", userAuth.getEmail());
            model.addAttribute("password", userAuth.getPassword());
            return "/profil";
        }
    }

    @GetMapping("/transfer")
    public String afficherPageTransfer(Model model) {
        logger.info("Tentative d'accès à la page des transactions.");
        try {
            logger.debug("Récupération des connexions et transactions.");
            List<User> connections = userService.getConnections();
            List<Transactions> transactions = transactionService.getAllTransactionById();
            
            logger.debug("Liste des connexion : {} ", connections);
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
