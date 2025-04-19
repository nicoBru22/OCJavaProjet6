package com.projet6.payMyBuddy.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.HeaderWriter;

/**
 * Configuration de la sécurité de l'application.
 * <p>
 * Cette classe configure les paramètres de sécurité de l'application en
 * utilisant Spring Security. Elle définit les règles d'accès pour les
 * différentes routes, configure l'authentification, la gestion des sessions, et
 * la gestion des mots de passe.
 * </p>
 * 
 * <strong>Services utilisés :</strong>
 * <ul>
 * <li>{@link CustomUserDetailService} : Service personnalisé pour récupérer les
 * détails de l'utilisateur pour l'authentification.</li>
 * </ul>
 * 
 * <strong>Composants de sécurité :</strong>
 * <ul>
 * <li>{@link BCryptPasswordEncoder} : Algorithme de hachage utilisé pour
 * sécuriser les mots de passe.</li>
 * <li>{@link AuthenticationManager} : Gestionnaire pour l'authentification des
 * utilisateurs.</li>
 * </ul>
 */
@Configuration
public class SecurityConfig {

	@Autowired
	private CustomUserDetailService customUserDetailsService;

	/**
	 * Configure les paramètres de sécurité HTTP de l'application.
	 * 
	 * Cette méthode définit les règles d'autorisation d'accès aux différentes URL
	 * en fonction des rôles de l'utilisateur. Elle configure également
	 * l'authentification par formulaire et OAuth2, la gestion des sessions et des
	 * headers HTTP.
	 *
	 * @param http L'objet {@link HttpSecurity} utilisé pour la configuration de la
	 *             sécurité HTTP.
	 * @return Un objet {@link SecurityFilterChain} qui contient la configuration de
	 *         la sécurité.
	 * @throws Exception Si une erreur se produit pendant la configuration de la
	 *                   sécurité.
	 */
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.headers(headers -> headers.addHeaderWriter((HeaderWriter) (request, response) -> response
						.setHeader("Content-Security-Policy", "frame-ancestors 'self'")))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/signin").permitAll()
						.requestMatchers("/users/add_user").permitAll()
						.requestMatchers("/profil").authenticated() 
						.requestMatchers("/connection").authenticated()
						.requestMatchers("/add_relation").authenticated()
						.requestMatchers("/users/add_connection").authenticated()
						.requestMatchers("/users/list_user").hasAnyRole("ADMIN")
						.requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
						.anyRequest().authenticated())
				.formLogin(login -> login.loginPage("/login").defaultSuccessUrl("/profil", true).permitAll())
				.oauth2Login(oauth -> oauth.loginPage("/login").defaultSuccessUrl("/profil", true))
				.logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
				.userDetailsService(customUserDetailsService);
		return http.build();
	}

	/**
	 * Crée un encodeur de mot de passe utilisant le bcrypt.
	 * 
	 * @return Un objet {@link PasswordEncoder} qui utilise l'algorithme BCrypt pour
	 *         sécuriser les mots de passe.
	 */
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Crée un gestionnaire d'authentification qui utilise
	 * {@link CustomUserDetailService} pour récupérer les détails des utilisateurs
	 * et vérifier leurs mots de passe.
	 * 
	 * @param http                  L'objet {@link HttpSecurity} utilisé pour la
	 *                              configuration de la sécurité HTTP.
	 * @param bCryptPasswordEncoder L'encodeur de mot de passe
	 *                              {@link BCryptPasswordEncoder}.
	 * @return Un objet {@link AuthenticationManager} utilisé pour gérer
	 *         l'authentification des utilisateurs.
	 * @throws Exception Si une erreur se produit pendant la configuration du
	 *                   gestionnaire d'authentification.
	 */
	@Bean
	AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder)
			throws Exception {
		AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
		authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(bCryptPasswordEncoder);
		return authenticationManagerBuilder.build();
	}
}
