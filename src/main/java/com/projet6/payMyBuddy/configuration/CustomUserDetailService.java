package com.projet6.payMyBuddy.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.projet6.payMyBuddy.model.User;
import com.projet6.payMyBuddy.repository.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Trouver l'utilisateur par email
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Retourner les détails de l'utilisateur
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            getGrantedAuthorities(user.getRole())
        );
    }

    // Convertir le rôle de l'utilisateur en autorité de Spring Security
    private List<GrantedAuthority> getGrantedAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role)); // Ajout du préfixe ROLE_
        return authorities;
    }
}
