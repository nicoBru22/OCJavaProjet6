package com.projet6.payMyBuddy.configuration;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

@Component
public class EnvConfig {
    public EnvConfig() {
        Dotenv dotenv = Dotenv.load();

        String clientId = dotenv.get("OAUTH_GITHUB_CLIENT_ID");
        String clientSecret = dotenv.get("OAUTH_GITHUB_CLIENT_SECRET");

        System.setProperty("OAUTH_GITHUB_CLIENT_ID", clientId);
        System.setProperty("OAUTH_GITHUB_CLIENT_SECRET", clientSecret);
    }
}