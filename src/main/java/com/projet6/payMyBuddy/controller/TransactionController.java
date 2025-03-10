package com.projet6.payMyBuddy.controller;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projet6.payMyBuddy.service.TransactionService;

/**
 * Contrôleur pour gérer les transactions dans l'application PayMyBuddy.
 * <p>
 * Ce contrôleur permet d'ajouter des transactions à l'application. Les
 * informations relatives à la transaction, telles que l'email de l'utilisateur,
 * la description et le montant, sont récupérées à partir des paramètres de la
 * requête.
 * </p>
 * 
 * <strong>Route gérée :</strong>
 * <ul>
 * <li>{@code /add_transaction} : Ajouter une transaction pour un
 * utilisateur.</li>
 * </ul>
 * 
 * <strong>Services utilisés :</strong>
 * <ul>
 * <li>{@link TransactionService} : Service pour gérer les transactions.</li>
 * </ul>
 * 
 * <strong>Journalisation :</strong>
 * <p>
 * La classe utilise Log4j2 pour enregistrer les événements liés à l'ajout de la
 * transaction.
 * </p>
 */
@Controller
public class TransactionController {

	private static final Logger logger = LogManager.getLogger(TransactionController.class);

	@Autowired
	private TransactionService transactionService;

	/**
	 * Ajoute une nouvelle transaction.
	 * 
	 * @param email       L'email de l'utilisateur impliqué dans la transaction.
	 * @param description La description de la transaction (par exemple, le motif).
	 * @param amount      Le montant de la transaction.
	 * @return Une réponse HTTP avec un statut 201 (Created) et un en-tête de
	 *         localisation vers la page de transfert.
	 * @throws Exception Si une erreur se produit lors de l'ajout de la transaction.
	 */
	@PostMapping("/add_transaction")
	public String addTransaction(@RequestParam String email,
	                             @RequestParam String description,
	                             @RequestParam double amount,
	                             RedirectAttributes redirectAttributes) {
	    logger.info("Tentative d'ajout d'une transaction. Email: {}, Description: {}, Montant: {}", email, description, amount);
	    try {
	        transactionService.addTransaction(email, description, amount);
	        logger.info("Transaction réussie pour l'email: {}", email);
	        redirectAttributes.addFlashAttribute("info", "La transaction a été réalisée avec succès.");
	        
	    } catch (RuntimeException e) {
	        logger.error("Erreur lors de l'ajout de la transaction : {}", e.getMessage());
	        redirectAttributes.addFlashAttribute("error", e.getMessage());
	    }
	    return "redirect:/transfer";
	}
}
