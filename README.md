# OCJavaProjet6

MODÈLE PHYSIQUE DE DONNÉE : 

-- Création de la base de données si elle n'existe pas déjà :

CREATE DATABASE IF NOT EXISTS OCProjet6_PayMyBuddy;

-- Sélection de la base de données : 

USE OCProjet6_PayMyBuddy;

-- Table des utilisateurs :

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- Clé primaire auto-incrémentée
    username VARCHAR(255) NOT NULL,       -- Nom d'utilisateur
    email VARCHAR(255) UNIQUE NOT NULL,  -- Adresse e-mail unique
    password VARCHAR(255) NOT NULL,      -- Mot de passe
);

-- Table des transactions :

CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- Clé primaire auto-incrémentée
    sender_id BIGINT NOT NULL,            -- Référence vers l'expéditeur
    receiver_id BIGINT NOT NULL,          -- Référence vers le destinataire
    description TEXT,                     -- Description de la transaction
    amount DECIMAL(10, 2) NOT NULL,       -- Montant de la transaction
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE, -- Contrainte FK
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE -- Contrainte FK
);

-- Table des connexions (Many-to-Many) :

CREATE TABLE IF NOT EXISTS user_connections (
    user_id BIGINT NOT NULL,       -- Référence au premier utilisateur
    connection_id BIGINT NOT NULL, -- Référence au second utilisateur
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (connection_id) REFERENCES users(id) ON DELETE CASCADE
);



