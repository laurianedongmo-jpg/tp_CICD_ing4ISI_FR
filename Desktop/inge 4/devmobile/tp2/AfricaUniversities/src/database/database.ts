import * as SQLite from 'expo-sqlite';
import { obtenirPrefixeTable } from '../private/preuveTravail';
import { CONFIG_ETUDIANT } from '../private/config';

// Générer le préfixe unique pour les tables
const PREFIX = obtenirPrefixeTable(
  CONFIG_ETUDIANT.nom,
  CONFIG_ETUDIANT.matricule,
  CONFIG_ETUDIANT.codeSecret
);

// Noms des tables avec préfixe
export const TABLE_PAYS = `${PREFIX}pays`;
export const TABLE_UNIVERSITES = `${PREFIX}universites`;

// Ouvrir la base de données de manière synchrone
const db = SQLite.openDatabaseSync('africauniversities.db');

/**
 * Initialiser la base de données
 */
export const initDatabase = async (): Promise<void> => {
  try {
    // Créer les tables en une seule commande
    db.execSync(`
      CREATE TABLE IF NOT EXISTS ${TABLE_PAYS} (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        flag TEXT NOT NULL,
        cca2 TEXT NOT NULL UNIQUE,
        population INTEGER
      );
      
      CREATE TABLE IF NOT EXISTS ${TABLE_UNIVERSITES} (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        country TEXT NOT NULL,
        name TEXT NOT NULL,
        web_page TEXT NOT NULL,
        domains TEXT
      );
    `);
    
    console.log('Base de données initialisée avec succès');
    console.log(`Table pays: ${TABLE_PAYS}`);
    console.log(`Table universités: ${TABLE_UNIVERSITES}`);
  } catch (error) {
    console.error('Erreur lors de l\'initialisation de la base de données:', error);
    throw error;
  }
};

/**
 * Sauvegarder les pays dans la base de données
 */
export const savePays = async (pays: any[]): Promise<void> => {
  try {
    const statement = db.prepareSync(
      `INSERT OR REPLACE INTO ${TABLE_PAYS} (name, flag, cca2, population) VALUES (?, ?, ?, ?)`
    );
    
    for (const country of pays) {
      statement.executeSync([
        country.name.common,
        country.flag,
        country.cca2,
        country.population || 0
      ]);
    }
    
    console.log(`${pays.length} pays sauvegardés`);
  } catch (error) {
    console.error('Erreur lors de la sauvegarde des pays:', error);
    throw error;
  }
};

/**
 * Récupérer tous les pays depuis la base de données
 */
export const getPays = async (): Promise<any[]> => {
  try {
    const result = db.getAllSync(`SELECT * FROM ${TABLE_PAYS} ORDER BY name`);
    return result as any[];
  } catch (error) {
    console.error('Erreur lors de la récupération des pays:', error);
    throw error;
  }
};

/**
 * Sauvegarder les universités dans la base de données
 */
export const saveUniversites = async (universites: any[], countryCode: string): Promise<void> => {
  try {
    // Supprimer les anciennes universités de ce pays
    db.runSync(`DELETE FROM ${TABLE_UNIVERSITES} WHERE country = ?`, [countryCode]);
    
    // Insérer les nouvelles universités
    const statement = db.prepareSync(
      `INSERT INTO ${TABLE_UNIVERSITES} (country, name, web_page, domains) VALUES (?, ?, ?, ?)`
    );
    
    for (const uni of universites) {
      statement.executeSync([
        countryCode,
        uni.name,
        uni.web_pages[0] || '',
        JSON.stringify(uni.domains || [])
      ]);
    }
    
    console.log(`${universites.length} universités sauvegardées pour ${countryCode}`);
  } catch (error) {
    console.error('Erreur lors de la sauvegarde des universités:', error);
    throw error;
  }
};

/**
 * Récupérer les universités d'un pays depuis la base de données
 */
export const getUniversites = async (countryName: string): Promise<any[]> => {
  try {
    const result = db.getAllSync(
      `SELECT * FROM ${TABLE_UNIVERSITES} WHERE country = ? ORDER BY name`,
      [countryName]
    );
    return (result as any[]).map((uni: any) => ({
      ...uni,
      domains: JSON.parse(uni.domains || '[]')
    }));
  } catch (error) {
    console.error('Erreur lors de la récupération des universités:', error);
    throw error;
  }
};

/**
 * Vérifier si des données existent dans la base
 */
export const hasPaysData = async (): Promise<boolean> => {
  try {
    const result = db.getFirstSync(`SELECT COUNT(*) as count FROM ${TABLE_PAYS}`) as any;
    return result.count > 0;
  } catch (error) {
    return false;
  }
};
