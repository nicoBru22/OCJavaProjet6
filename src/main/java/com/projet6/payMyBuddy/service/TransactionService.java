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
    public Transactions addTransaction(String email, String description, Double amount) throws Exception {
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
                Transactions newTransaction = new Transactions();
                newTransaction.setSender(sender);
                newTransaction.setReceiver(receiver);
                newTransaction.setDescription(description);
                newTransaction.setAmount(amount);

                logger.debug("La nouvelle transaction à sauvegarder : {}", newTransaction);
                return transactionRepository.save(newTransaction);
            }
        } catch (Exception e) {
            logger.error("Une erreur est survenue lors de l'ajout d'une nouvelle transaction.", e);
            throw new Exception("Une erreur est survenue lors de l'ajout d'une nouvelle transaction.", e);
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
