# OCJavaProjet6

MODÈLE PHYSIQUE DE DONNÉE : 

-- Création de la base de données si elle n'existe pas déjà :

CREATE DATABASE IF NOT EXISTS OCProjet6_PayMyBuddy;

-- Sélection de la base de données : 

USE OCProjet6_PayMyBuddy;

-- Table des utilisateurs :

CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL DEFAULT nextval('users_id_seq'::regclass),  -- Clé primaire auto-incrémentée
    username VARCHAR(255) NOT NULL, -- Nom d'utilisateur, obligatoire
    email VARCHAR(255) NOT NULL,-- Email, obligatoire
    password VARCHAR(255) NOT NULL,-- Mot de passe, obligatoire
    role VARCHAR(255), -- Rôle de l'utilisateur (facultatif)
    solde DOUBLE PRECISION NOT NULL DEFAULT 0,-- Solde, obligatoire, valeur par défaut 0
    CONSTRAINT users_pkey PRIMARY KEY (id) -- Clé primaire
);



-- Table des transactions :

CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT NOT NULL DEFAULT nextval('transactions_id_seq'::regclass),  -- Clé primaire auto-incrémentée
    sender_id BIGINT NOT NULL, -- ID de l'utilisateur envoyeur, obligatoire
    receiver_id BIGINT NOT NULL,  -- ID de l'utilisateur destinataire, obligatoire
    description VARCHAR(255),  -- Description de la transaction, facultative
    amount DOUBLE PRECISION NOT NULL, -- Montant de la transaction, obligatoire
    bank_commission DOUBLE PRECISION,-- Commission bancaire, facultative
    total_amount DOUBLE PRECISION, -- Montant total, facultatif
    CONSTRAINT transactions_pkey PRIMARY KEY (id), -- Clé primaire
    CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,  -- Clé étrangère vers users (envoyeur)
    CONSTRAINT fk_receiver FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE  -- Clé étrangère vers users (destinataire)
);



-- Table des connexions (Many-to-Many) :

CREATE TABLE IF NOT EXISTS users_connexion (
    id BIGINT NOT NULL DEFAULT nextval('users_connexion_id_seq'::regclass),  -- Clé primaire auto-incrémentée
    user_id BIGINT NOT NULL, -- ID de l'utilisateur (utilisateur connecté), obligatoire
    connected_user_id BIGINT NOT NULL, -- ID de l'utilisateur connecté (destinataire), obligatoire
    CONSTRAINT users_connexion_pkey PRIMARY KEY (id), -- Clé primaire sur la colonne id
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,  -- Clé étrangère vers users (utilisateur), avec suppression en cascade
    CONSTRAINT fk_connected_user_id FOREIGN KEY (connected_user_id) REFERENCES users(id) ON DELETE CASCADE, -- Clé étrangère vers users (utilisateur connecté), avec suppression en cascade
    -- Assurer qu'un utilisateur ne peut pas être connecté deux fois à un autre utilisateur
    CONSTRAINT unique_user_connection UNIQUE (user_id, connected_user_id)  -- Contrainte d'unicité pour éviter les doublons dans les connexions
);




