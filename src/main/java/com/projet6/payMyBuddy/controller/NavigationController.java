package com.projet6.payMyBuddy.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.projet6.payMyBuddy.model.Transactions;
import com.projet6.payMyBuddy.model.User;
import com.projet6.payMyBuddy.service.TransactionService;
import com.projet6.payMyBuddy.service.UserService;

@Controller
public class NavigationController {

    private static final Logger logger = LogManager.getLogger(NavigationController.class);

    @Autowired
    public UserService userService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/")
    public String afficherPageAccueil(Model model) {
        logger.info("Accès à la page d'accueil.");
        return "profil";
    }

    @GetMapping("/login")
    public String afficherPageLogIn(Model model) {
        logger.info("Accès à la page de connexion.");
        return "login";
    }

    @GetMapping("/signin")
    public String afficherPageSignIn(Model model) {
        logger.info("Accès à la page d'inscription.");
        return "signin";
    }

    @GetMapping("/add_relation")
    public String afficherPageAddRelation(Model model) {
        logger.info("Accès à la page d'ajout de relation.");
        return "add_relation";
    }

    @GetMapping("/profil")
    public String afficherPageUserProfil(Model model) {
        logger.info("Tentative d'accès au profil utilisateur.");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            logger.debug("Utilisateur authentifié détecté.");
            User currentUser = userService.getCurrentUser();

            if (currentUser != null) {
                logger.info("Utilisateur trouvé : {}", currentUser.getUsername());
                model.addAttribute("username", currentUser.getUsername());
                model.addAttribute("email", currentUser.getEmail());
                model.addAttribute("password", currentUser.getPassword());
                return "profil";
            } else {
                logger.error("Utilisateur non trouvé dans la base de données.");
                model.addAttribute("error", "Utilisateur non trouvé.");
                return "signin";
            }
        } else {
            logger.error("Utilisateur non authentifié.");
            model.addAttribute("error", "Utilisateur non authentifié.");
            return "login";
        }
    }

    @GetMapping("/transfer")
    public String afficherPageTransfer(Model model) {
        logger.info("Accès à la page de transfert.");
        try {
            logger.debug("Récupération des connexions et transactions.");
            List<User> connections = userService.getConnections();
            List<Transactions> transactions = transactionService.getAllTransactionById();

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
