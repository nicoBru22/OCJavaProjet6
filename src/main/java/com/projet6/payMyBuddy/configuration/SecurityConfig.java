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

@Configuration
public class SecurityConfig {
	
	@Autowired
	private CustomUserDetailService customUserDetailsService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http.csrf(csrf -> csrf.disable())
	        .headers(headers -> headers.addHeaderWriter((HeaderWriter) (request, response) -> 
	            response.setHeader("Content-Security-Policy", "frame-ancestors 'self'")
	        ))
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers("/h2-console/**").permitAll()
	            .requestMatchers("/signin").permitAll()
	            .requestMatchers("/login").permitAll()
	            .requestMatchers("/profil").authenticated()
	            .requestMatchers("/connection").authenticated()
	            .requestMatchers("/add_relation").authenticated()
	            .requestMatchers("/users/add_user").permitAll()
	            .requestMatchers("/users/add_connection").authenticated()
	            .requestMatchers("/users/list_user").hasAnyRole("ADMIN")
	            .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
	            .anyRequest().authenticated()
	        )
	        .formLogin(login -> login
	            .loginPage("/login")
	            .defaultSuccessUrl("/profil", true).permitAll()
	        )
	        .oauth2Login(oauth -> oauth
	            .loginPage("/login")
	            .defaultSuccessUrl("/profil", true)
	        )
	        .logout(logout -> logout
	            .logoutUrl("/logout")
	            .logoutSuccessUrl("/login?logout").permitAll()
	        )
	        .userDetailsService(customUserDetailsService);

	    return http.build();
	}


    @Bean
    PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder)
            throws Exception {
		AuthenticationManagerBuilder authenticationManagerBuilder = http
				.getSharedObject(AuthenticationManagerBuilder.class);
		authenticationManagerBuilder.userDetailsService(customUserDetailsService)
				.passwordEncoder(bCryptPasswordEncoder);
		return authenticationManagerBuilder.build();
	}
}
