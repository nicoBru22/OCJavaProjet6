package com.projet6.payMyBuddy.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projet6.payMyBuddy.exception.InvalidRequestException;
import com.projet6.payMyBuddy.exception.SoldeInvalidException;
import com.projet6.payMyBuddy.model.Transactions;
import com.projet6.payMyBuddy.model.User;
import com.projet6.payMyBuddy.repository.TransactionRepository;
import com.projet6.payMyBuddy.repository.UserRepository;

/**
 * Service pour gérer les transactions entre utilisateurs.
 * Fournit des méthodes pour récupérer, ajouter et gérer les transactions.
 */
@Service
public class TransactionService {

    private Logger logger = LogManager.getLogger(TransactionService.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Récupère toutes les transactions disponibles dans la base de données.
     *
     * @return une liste de toutes les transactions.
     * @throws Exception si une erreur survient lors de la récupération des transactions.
     */
    public List<Transactions> getAllTransactions() {
        logger.debug("Entrée dans la méthode getAllTransactions() de la classe TransactionService.");
        List<Transactions> transactionsList = transactionRepository.findAll();
        logger.debug("La liste des transactions : {}", transactionsList);
        logger.info("Liste des transactions récupérée avec succès.");
        return transactionsList;
    }

    /**
     * Ajoute une nouvelle transaction entre l'utilisateur courant et un destinataire.
     *
     * @param email l'email du destinataire.
     * @param description une description de la transaction.
     * @param amount le montant de la transaction.
     * @return la transaction créée et sauvegardée.
     * @throws Exception si une erreur survient lors de l'ajout de la transaction ou si le destinataire est introuvable.
     */
    @Transactional
    public Transactions addTransaction(String email, String description, double amount)  {
        logger.info("Entrée dans la méthode addTransaction de la classe TransactionService.");
        logger.debug("Les données en paramètre : email = {}, description = {}, amount = {}", email, description, amount);
        
        if (email.isBlank()) {
            logger.error("L'email du destinataire est obligatoire. Email = {}.", email);
            throw new InvalidRequestException("L'email du destinataire est obligatoire. Email : "+ email);
        }
        if (amount <= 0) {
            logger.error("Le montant est obligatoire et doit être supérieur à 0. Montant = {}.", amount);
            throw new InvalidRequestException("Le montant est obligatoire et doit être supérieur à 0. Montant : "+ amount);
        }
        
        User sender = userService.getCurrentUser();
        User receiver = userRepository.findByEmail(email);

        double bankCommission = getBankCommission(amount);
        double totalTransactionAmount = bankCommission + amount;

        logger.debug("Commission bancaire calculée : {}. Montant total de la transaction : {}", bankCommission, totalTransactionAmount);

        double senderSolde = sender.getSolde();
        double senderNewSolde = senderSolde - totalTransactionAmount;

        if (senderNewSolde < 0) {
            logger.error("Solde insuffisant. Solde actuel = {}, Montant à débiter = {}", senderSolde, totalTransactionAmount);
            throw new SoldeInvalidException("Solde insuffisant. Solde actuel = "+ senderSolde + ", Montant à débiter = "+ totalTransactionAmount);
        }

        double receiverSolde = receiver.getSolde();
        double receiverNewSolde = receiverSolde + amount;

        sender.setSolde(senderNewSolde);
        receiver.setSolde(receiverNewSolde);

        logger.debug("Nouveaux soldes : envoyeur = {}, destinataire = {}", senderNewSolde, receiverNewSolde);

        Transactions newTransaction = new Transactions();
        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setDescription(description);
        newTransaction.setAmount(amount);
        newTransaction.setBankCommission(bankCommission);
        newTransaction.setTotalAmount(totalTransactionAmount);

        Transactions savedTransaction = transactionRepository.save(newTransaction);
        
        logger.info("Transaction réussie : ID = {}, Montant = {}, Envoyeur = {}, Destinataire = {}", 
                     savedTransaction.getId(), amount, sender.getEmail(), receiver.getEmail());
        
        return savedTransaction;
    }

    
    /**
     * Calcule la commission de la banque sur la transaction.
     * 
     * 
     * @param amount montant de la transaction.
     * @return le montant de la commission que prend la banque.
     * @exception si une erreur survient lors de la récupération de la commission bancaire.
     */
    public double getBankCommission(double amount) {
    	logger.debug("Entrée dans la méthode TransactionService.getBankCommission");
		logger.debug("Tentative de calculer la commission bancaire pour le montant : {} ", amount);
		
		double bankCommission = 0.05;
		double resultBankCommission = amount * bankCommission;
        
		logger.info("Le montant de la commission est de : {}, pour une transaction de : {} ", resultBankCommission, amount);	
    	return resultBankCommission; 	
    }

    /**
     * Récupère toutes les transactions associées à l'utilisateur actuellement connecté.
     *
     * @return une liste de transactions dont l'utilisateur courant est l'émetteur.
     * @throws Exception si une erreur survient lors de la récupération des transactions.
     */
    public List<Transactions> getAllTransactionById() {
        logger.debug("Entrée dans la méthode getAllTransactionById() de la classe TransactionService.");
        
        User user = userService.getCurrentUser();
        logger.debug("L'utilisateur courant est : {}", user);
        
        List<Transactions> transactionUser = transactionRepository.findBySender(user);
        List<Transactions> transactionReceiver = transactionRepository.findByReceiver(user);
        
        List<Transactions> transactionList = new ArrayList<>(transactionUser);
        transactionList.addAll(transactionReceiver);
        
        if(transactionList.isEmpty()) {
        	logger.warn("La liste est vide : {}", transactionList);
        }
        
        logger.info("La liste des transactions : {}", transactionList);
        return transactionList;
    }
}
