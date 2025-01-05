package com.projet6.payMyBuddy.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.HeaderWriter;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.headers(headers -> {
					headers.addHeaderWriter((HeaderWriter) (request, response) -> response
							.setHeader("Content-Security-Policy", "frame-ancestors 'self'"));
				}).authorizeHttpRequests(auth -> auth
						.requestMatchers("/h2-console/**").permitAll()
						.requestMatchers("/signin").permitAll()
						.requestMatchers("/login").permitAll()
						.requestMatchers("/profil").authenticated()
						.requestMatchers("/add_relation").authenticated()
						.requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
						.anyRequest().authenticated()
				)
				.formLogin(login -> login
			            .loginPage("/login")   
						.defaultSuccessUrl("/h2-console", true)
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/login?logout")
						.permitAll())
				.userDetailsService(userDetailsService())

		;

		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
	    return new InMemoryUserDetailsManager(
	        User.withUsername("test@example.com")        // Crée un utilisateur avec l'email "test@example.com"
	            .password(passwordEncoder().encode("password"))  // Utilise un encodeur de mot de passe pour encoder le mot de passe "password"
	            .roles("USER")                           // Assigne le rôle "USER" à l'utilisateur
	            .build()                                 // Crée l'utilisateur
	    );
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
