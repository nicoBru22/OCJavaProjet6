package com.projet6.payMyBuddy.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.projet6.payMyBuddy.service.TransactionService;

@Controller
public class TransactionController {
	
	private Logger logger = LogManager.getLogger(TransactionController.class);
	
	@Autowired
	private TransactionService transactionService;

	@PostMapping("/add_transaction")
	public String addTransaction(@RequestParam String email, @RequestParam String description, @RequestParam double amount) throws Exception {
		logger.info("Entrée dans la méthode addTransaction de la class TransactionCOntroller.");
		logger.debug("les données en paramètre. email : {}, description :{}, amount : {} ", email, description, amount);
			transactionService.addTransaction(email, description, amount);
			return "redirect:/transfer";

	}
	
	
	
	
	
	
}
