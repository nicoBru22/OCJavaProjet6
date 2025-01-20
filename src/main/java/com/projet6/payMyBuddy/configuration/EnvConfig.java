package com.projet6.payMyBuddy.configuration;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

@Component
public class EnvConfig {
    public EnvConfig() {
        // Charger les variables d'environnement depuis le fichier .env
        Dotenv dotenv = Dotenv.load(); 
    }
}

