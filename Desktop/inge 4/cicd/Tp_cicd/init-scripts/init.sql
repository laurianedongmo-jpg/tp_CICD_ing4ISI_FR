-- Script d'initialisation de la base de données
-- Ce script sera exécuté automatiquement au premier démarrage de MySQL

CREATE DATABASE IF NOT EXISTS tp_cicd_db;

USE tp_cicd_db;

-- Exemple de table (à adapter selon vos besoins)
-- CREATE TABLE IF NOT EXISTS users (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     username VARCHAR(50) NOT NULL UNIQUE,
--     email VARCHAR(100) NOT NULL UNIQUE,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
-- );

-- Insérer des données de test (optionnel)
-- INSERT INTO users (username, email) VALUES 
--     ('admin', 'admin@example.com'),
--     ('user1', 'user1@example.com');

-- Afficher un message de confirmation
SELECT 'Database initialized successfully!' AS message;
