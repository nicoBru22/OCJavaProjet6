package com.projet6.payMyBuddy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

/**
 * Représente une transaction financière entre deux utilisateurs.
 * 
 * <p>
 * Cette classe est une entité JPA qui mappe les informations d'une transaction
 * dans la base de données. Elle inclut un identifiant unique, les utilisateurs
 * impliqués (expéditeur et récepteur), une description de la transaction et le
 * montant associé.
 * </p>
 * 
 * <p>
 * Les relations entre les entités sont définies par des annotations JPA. Chaque
 * transaction est associée à deux utilisateurs : un expéditeur et un récepteur.
 * </p>
 * 
 * <p>
 * Lombok est utilisé pour générer automatiquement les getters, setters, et
 * autres méthodes utilitaires via l'annotation {@code @Data}.
 * </p>
 * 
 * <ul>
 * <li>{@code id} : Identifiant unique de la transaction, généré
 * automatiquement.</li>
 * <li>{@code sender} : L'utilisateur qui envoie la transaction.</li>
 * <li>{@code receiver} : L'utilisateur qui reçoit la transaction.</li>
 * <li>{@code description} : Une description textuelle de la transaction.</li>
 * <li>{@code amount} : Le montant de la transaction.</li>
 * </ul>
 */

@Entity
@Data
public class Transactions {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne
	@JoinColumn(name = "sender_id", unique = false)
	private User sender;

	@ManyToOne
	@JoinColumn(name = "receiver_id", unique = false)
	private User receiver;

	@Column
	private String description;

	@Column
	private double amount;
	
	@Column(nullable = false)
	private Double bankCommission = 0.0;
	
	@Column
	private double totalAmount;

}
