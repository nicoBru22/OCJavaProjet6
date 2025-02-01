package com.projet6.payMyBuddy.service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<Transactions> getAllTransactions() throws Exception {
        logger.info("Entrée dans la méthode getAllTransactions() de la classe TransactionService.");
        try {
            List<Transactions> transactionsList = transactionRepository.findAll();
            logger.debug("La liste des transactions : {}", transactionsList);
            return transactionsList;
        } catch (Exception e) {
            logger.error("Une erreur est survenue lors de la récupération de toutes les transactions.", e);
            throw new Exception("Une erreur est survenue lors de la récupération de toutes les transactions.");
        }
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
    public Transactions addTransaction(String email, String description, double amount) throws Exception {
        logger.info("Entrée dans la méthode addTransaction de la classe TransactionService.");
        logger.debug("Les données en paramètre : email : {}, description : {}, amount : {}", email, description, amount);
        try {
            User sender = userService.getCurrentUser();
            User receiver = userRepository.findByEmail(email);

            logger.debug("L'utilisateur émetteur (sender) : {}", sender);
            logger.debug("L'utilisateur destinataire (receiver) : {}", receiver);

            if (receiver == null) {
                logger.error("Le destinataire avec l'email {} n'existe pas.", email);
                throw new Exception("Destinataire introuvable.");
            } else {
            	double bankCommission = getBankCommission(amount);
            	double amountTransactionTotal = bankCommission + amount;
            	
            	double senderSolde = sender.getSolde();
            	double senderNewSolde = senderSolde - amount;
            	double receiverSolde = receiver.getSolde();
            	double receiverNewSolde = receiverSolde + amount;
            	
            	if(senderNewSolde < 0) {
            		logger.error("Le solde de l'envoyeur ne peut pas être inférieur à 0.");
            		throw new IllegalArgumentException("Le solde de l'envoyeur ne peut pas être inférieur à 0.");
            	} else {
                	sender.setSolde(senderNewSolde);
                	receiver.setSolde(receiverNewSolde);
            	}         	
            	
                Transactions newTransaction = new Transactions();
                newTransaction.setSender(sender);
                newTransaction.setReceiver(receiver);
                newTransaction.setDescription(description);
                newTransaction.setAmount(amount);
                newTransaction.setBankCommission(bankCommission);
                newTransaction.setTotalAmount(amountTransactionTotal);

                logger.debug("La nouvelle transaction à sauvegarder : {}", newTransaction);
                return transactionRepository.save(newTransaction);
            }
        } catch (Exception e) {
            logger.error("Une erreur est survenue lors de l'ajout d'une nouvelle transaction.", e.getMessage());
            throw new Exception("Une erreur est survenue lors de l'ajout d'une nouvelle transaction.", e);
        }
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
    	logger.info("Entrée dans la méthode TransactionService.getBankCommission");
    	try {
    		logger.debug("Tentative de calculer la commission bancaire pour le montant : {} ", amount);
    		double bankCommission = 0.0005;
    		double resultBankCommission = amount * bankCommission;
            
    		logger.debug("Le montant de la commission est de : {}, pour une transaction de : {} ", resultBankCommission, amount);	
        	return resultBankCommission;
    	} catch (Exception e) {
    		logger.error("Une erreur est survenue lors de la réupération de la commission bancaire avec le montant : {} ", amount);
    		throw new RuntimeException("Une erreur est survenue lors de la réupération de la commission bancaire." + e);
    	}    	
    }

    /**
     * Récupère toutes les transactions associées à l'utilisateur actuellement connecté.
     *
     * @return une liste de transactions dont l'utilisateur courant est l'émetteur.
     * @throws Exception si une erreur survient lors de la récupération des transactions.
     */
    public List<Transactions> getAllTransactionById() throws Exception {
        logger.info("Entrée dans la méthode getAllTransactionById() de la classe TransactionService.");
        try {
            User user = userService.getCurrentUser();
            logger.debug("L'utilisateur courant est : {}", user);
            List<Transactions> transactionList = transactionRepository.findBySender(user);
            logger.debug("La liste des transactions : {}", transactionList);
            return transactionList;
        } catch (Exception e) {
            logger.error("Une erreur est survenue lors de la récupération des transactions.", e);
            throw new Exception("Une erreur est survenue lors de la récupération des transactions.", e);
        }
    }
}
