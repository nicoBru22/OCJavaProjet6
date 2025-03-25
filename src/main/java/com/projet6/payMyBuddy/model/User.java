package com.projet6.payMyBuddy.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Représente un utilisateur dans le système.
 * 
 * <p>
 * Cette classe est une entité JPA qui mappe les informations des utilisateurs
 * dans la base de données. Elle contient des informations essentielles comme
 * l'identifiant, le nom d'utilisateur, l'email, le mot de passe, le rôle et une
 * liste de connexions avec d'autres utilisateurs.
 * </p>
 * 
 * <p>
 * Les relations entre les entités, les contraintes d'unicité et de nullabilité
 * des colonnes sont définies par les annotations JPA. Une relation
 * {@code @ManyToMany} est utilisée pour gérer les connexions entre
 * utilisateurs, avec une table d'association nommée {@code user_connections}.
 * </p>
 * 
 * <ul>
 * <li>{@code id} : Identifiant unique de l'utilisateur, généré
 * automatiquement.</li>
 * <li>{@code username} : Nom d'utilisateur, ne peut pas être nul.</li>
 * <li>{@code email} : Adresse e-mail unique et non nulle.</li>
 * <li>{@code password} : Mot de passe de l'utilisateur, ne peut pas être
 * nul.</li>
 * <li>{@code role} : Rôle de l'utilisateur dans le système (par exemple,
 * "admin" ou "user").</li>
 * <li>{@code connections} : Liste des connexions associées à cet utilisateur,
 * représentant une relation bidirectionnelle avec d'autres utilisateurs.</li>
 * </ul>
 * 
 * <p>
 * Lombok est utilisé pour générer automatiquement les getters, setters, la
 * méthode {@code toString()}, et d'autres méthodes utilitaires via l'annotation
 * {@code @Data}. L'attribut {@code connections} est exclu de la méthode
 * {@code toString()} pour éviter les boucles infinies lors de la
 * journalisation.
 * </p>
 * 
 * <p>
 * <strong>Table : </strong> {@code users}
 * </p>
 * <p>
 * <strong>Table d'association pour {@code connections} : </strong>
 * {@code user_connections}
 * </p>
 * 
 * @author [Votre Nom]
 */

@Entity
@Data
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)
	private String username;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false, columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
	private double solde = 0;

	private String role;

	@ManyToMany
	@JoinTable(name = "user_connections", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "connection_id"))
	@ToString.Exclude
	private List<User> connections;
}
