package com.projet6.payMyBuddy.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projet6.payMyBuddy.model.Transactions;
import com.projet6.payMyBuddy.model.User;
import com.projet6.payMyBuddy.repository.TransactionRepository;
import com.projet6.payMyBuddy.repository.UserRepository;

@Service
public class TransactionService {
	
	private Logger logger = LogManager.getLogger(TransactionService.class);
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;

	public List<Transactions> getAllTransactions() throws Exception {
		logger.info("Entrée dans la méthode getAllTransactions() de la class TransactionService.");
		try {
			List<Transactions> transactionsList = transactionRepository.findAll();
			logger.debug("La liste des transactions : {}", transactionsList);
			return transactionsList;
		} catch (Exception e) {
			logger.error("Une erreur est survenue lors de la récupération de toutes les transacations." + e);
			throw new Exception("Une erreur est survenue lors de la récupération de toutes les transacations." + e);
		}
	}
	
	public Transactions addTransaction(String email, String description, Double amount) throws Exception {
		logger.info("Entrée dans la méthode addTransaction de la class TransactionService.");
		logger.debug("les données en paramètre. email : {}, description :{}, amount : {} ", email, description, amount);
		
		User sender = userService.getCurrentUser();
		User receiver = userRepository.findByEmail(email);
		try {
			Transactions newTransaction = new Transactions();
			newTransaction.setSender(sender);
			newTransaction.setReceiver(receiver);
			newTransaction.setDescription(description);
			newTransaction.setAmount(amount);
			
			logger.debug("La nouvelle transaction à sauvegarder : {} ", newTransaction);
			return transactionRepository.save(newTransaction);
		} catch (Exception e) {
			logger.error("Une erreur est survenue lors de l'ajout d'une nouvelle transacation avec les paramètres : {}, {}, {}, {}, {} ", email, amount, description, receiver, sender);
			throw new Exception("Une erreur est survenue lors de l'ajout d'une nouvelle transacation");
		}
	}
	
	
}
