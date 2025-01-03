package com.projet6.payMyBuddy.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.HeaderWriter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Désactive CSRF (nécessaire pour H2)
            .headers(headers -> {
                // Utilisation de addHeaderWriter pour personnaliser les en-têtes HTTP
                headers.addHeaderWriter((HeaderWriter) (request, response) -> 
                    response.setHeader("Content-Security-Policy", "frame-ancestors 'self'")); // Permet les iframes du même domaine
            })
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll() // Autorise l'accès à la console H2 sans authentification
                .anyRequest().authenticated() // Protège les autres URLs
            )
            .formLogin(login -> login
                .defaultSuccessUrl("/h2-console", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}

