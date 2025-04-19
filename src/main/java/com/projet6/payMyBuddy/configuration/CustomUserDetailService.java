package com.projet6.payMyBuddy.configuration;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.projet6.payMyBuddy.model.User;
import com.projet6.payMyBuddy.repository.UserRepository;

/**
 * Service personnalisé pour charger les détails d'un utilisateur.
 * <p>
 * Cette classe implémente l'interface {@link UserDetailsService} et permet de
 * charger un utilisateur par son email dans le cadre de l'authentification
 * Spring Security. Elle récupère l'utilisateur à partir du
 * {@link UserRepository} et lui attribue les rôles nécessaires pour
 * l'authentification.
 * </p>
 * <p>
 * Cette classe contient également la gestion des rôles des utilisateurs, qui
 * sont attribués à chaque utilisateur en fonction de son rôle.
 * </p>
 */
@Service
public class CustomUserDetailService implements UserDetailsService {

	private static final Logger logger = LogManager.getLogger(CustomUserDetailService.class);

	@Autowired
	private UserRepository userRepository;

	/**
	 * Charge un utilisateur en fonction de son email.
	 * 
	 * Cette méthode est utilisée par Spring Security pour effectuer
	 * l'authentification. Elle recherche l'utilisateur dans la base de données via
	 * le {@link UserRepository} et retourne les informations de l'utilisateur sous
	 * forme de {@link UserDetails}.
	 * 
	 * @param email L'email de l'utilisateur à charger.
	 * @return Un objet {@link UserDetails} contenant les informations de
	 *         l'utilisateur.
	 * @throws UsernameNotFoundException Si l'utilisateur n'est pas trouvé dans la
	 *                                   base de données.
	 */
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		logger.info("Tentative de chargement de l'utilisateur avec l'email : {}", email);
		User user = userRepository.findByEmail(email);
		if (user == null) {
			logger.error("Utilisateur non trouvé pour l'email : {}", email);
			throw new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email);
		}
		logger.info("Utilisateur trouvé : {}", email);

		// Conversion en UserDetails
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
				getGrantedAuthorities(user.getRole()));
	}

	/**
	 * Attribue les rôles à un utilisateur en fonction de son rôle.
	 * 
	 * Cette méthode crée une liste de {@link GrantedAuthority} qui représente les
	 * rôles de l'utilisateur dans le système de sécurité de Spring. Elle ajoute le
	 * rôle sous forme de "ROLE_<role>".
	 * 
	 * @param role Le rôle de l'utilisateur.
	 * @return Une liste d'objets {@link GrantedAuthority} représentant les rôles de
	 *         l'utilisateur.
	 */
	private List<GrantedAuthority> getGrantedAuthorities(String role) {
		logger.info("Assignation des rôles pour l'utilisateur avec le rôle : {}", role);
		List<GrantedAuthority> authorities = new ArrayList<>();
		try {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
		} catch (Exception e) {
			logger.error("Erreur lors de l'ajout des rôles pour : {}", role, e);
			throw new RuntimeException("Erreur lors de l'attribution des rôles", e);
		}
		return authorities;
	}
}
