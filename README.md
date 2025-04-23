# OCJavaProjet6

MODÈLE PHYSIQUE DE DONNÉE : 

-- Création de la base de données si elle n'existe pas déjà :

CREATE DATABASE OCProjet6_PayMyBuddy;

-- Sélection de la base de données : 

USE OCProjet6_PayMyBuddy;

-- Table des utilisateurs :

CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255),
    solde DOUBLE PRECISION NOT NULL DEFAULT 0
);



-- Table des transactions :

CREATE TABLE transactions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    description VARCHAR(255),
    amount DOUBLE PRECISION,
    bank_commission DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    total_amount DOUBLE PRECISION,
    CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_receiver FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE
);




-- Table des connexions (jointure Many-to-Many) :

CREATE TABLE user_connections (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,  -- Clé primaire auto-générée
    user_id BIGINT NOT NULL,  -- ID de l'utilisateur (utilisateur connecté), obligatoire
    connected_user_id BIGINT NOT NULL,  -- ID de l'utilisateur connecté (destinataire), obligatoire
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,  -- Clé étrangère vers users (utilisateur), avec suppression en cascade
    CONSTRAINT fk_connected_user_id FOREIGN KEY (connected_user_id) REFERENCES users(id) ON DELETE CASCADE, -- Clé étrangère vers users (utilisateur connecté), avec suppression en cascade
    CONSTRAINT unique_user_connection UNIQUE (user_id, connected_user_id)  -- Contrainte d'unicité pour éviter les doublons dans les connexions
);
