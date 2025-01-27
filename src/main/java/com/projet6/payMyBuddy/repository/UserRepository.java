package com.projet6.payMyBuddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projet6.payMyBuddy.model.User;

/**
 * Repository pour gérer les utilisateurs dans l'application PayMyBuddy.
 * <p>
 * Cette interface étend {@link JpaRepository}, permettant des opérations CRUD
 * (création, lecture, mise à jour, suppression) sur l'entité {@link User}. Elle
 * permet également de définir des méthodes personnalisées pour récupérer des
 * utilisateurs en fonction des critères spécifiés.
 * </p>
 * 
 * <strong>Routes :</strong>
 * <ul>
 * <li>{@code findByEmail(String email)} : Récupérer un utilisateur par son
 * adresse email.</li>
 * </ul>
 * 
 * <strong>Services utilisés :</strong>
 * <ul>
 * <li>{@link User} : Entité représentant un utilisateur dans le système.</li>
 * </ul>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * Récupère un utilisateur en fonction de son adresse email.
	 * 
	 * @param email L'adresse email de l'utilisateur à récupérer.
	 * @return Un utilisateur correspondant à l'adresse email spécifiée.
	 */
	User findByEmail(String email);
}
