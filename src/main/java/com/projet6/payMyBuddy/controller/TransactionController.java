package com.projet6.payMyBuddy.controller;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projet6.payMyBuddy.service.TransactionService;

import jakarta.servlet.http.HttpServletResponse;

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
	public ResponseEntity<String> addTransaction(@RequestParam String email,
	                                             @RequestParam String description,
	                                             @RequestParam double amount,
	                                             RedirectAttributes redirectAttributes) {
	    try {
	        transactionService.addTransaction(email, description, amount);

	        // Redirection vers /transfer avec statut 302 (Found)
	        return ResponseEntity.status(HttpStatus.CREATED)
	                .header(HttpHeaders.LOCATION, "/transfer")
	                .build();

	    } catch (IllegalArgumentException e) {
	        // Ajout du message d'erreur dans les attributs de redirection
	        redirectAttributes.addFlashAttribute("error", e.getMessage());
	        
	        // Redirection vers /transfer avec erreur
	        return ResponseEntity.status(HttpStatus.FOUND)
	                .header(HttpHeaders.LOCATION, "/transfer")
	                .build();
	        
	    } catch (Exception e) {
	        // Ajout du message d'erreur dans les attributs de redirection
	        redirectAttributes.addFlashAttribute("error", e.getMessage());
	        
	        // Redirection vers /transfer en cas d'exception générale
	        return ResponseEntity.status(HttpStatus.FOUND)
	                .header(HttpHeaders.LOCATION, "/transfer")
	                .build();
	    }
	}

}
