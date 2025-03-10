package com.projet6.payMyBuddy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projet6.payMyBuddy.model.Transactions;
import com.projet6.payMyBuddy.model.User;

/**
 * Repository pour gérer les transactions dans l'application PayMyBuddy.
 * <p>
 * Cette interface étend {@link JpaRepository}, permettant des opérations CRUD
 * (création, lecture, mise à jour, suppression) sur l'entité
 * {@link Transactions}. Elle permet également de définir des méthodes
 * personnalisées pour récupérer des transactions en fonction des critères
 * spécifiés.
 * </p>
 * 
 * <strong>Route :</strong>
 * <ul>
 * <li>{@code findBySender(User user)} : Récupérer toutes les transactions
 * envoyées par un utilisateur spécifié.</li>
 * </ul>
 * 
 * <strong>Services utilisés :</strong>
 * <ul>
 * <li>{@link Transactions} : Entité représentant une transaction.</li>
 * <li>{@link User} : Entité représentant un utilisateur dans le système.</li>
 * </ul>
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transactions, Long> {

	/**
	 * Récupère toutes les transactions envoyées par un utilisateur donné.
	 * 
	 * @param user L'utilisateur dont on souhaite récupérer les transactions
	 *             envoyées.
	 * @return Une liste de transactions envoyées par l'utilisateur spécifié.
	 */
	List<Transactions> findBySender(User user);
	List<Transactions> findByReceiver(User user);
}
