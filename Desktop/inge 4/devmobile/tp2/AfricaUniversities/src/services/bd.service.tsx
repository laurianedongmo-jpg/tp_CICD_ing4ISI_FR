import * as SQLite from 'expo-sqlite';
import { Pays } from '../interfaces/Pays.interface';
import { Universite } from '../interfaces/Universite.interface';
import { obtenirSignatureEtudiant } from '../private/preuveTravail';

// Service pour gérer la base de données SQLite
// J'ai implémenté les opérations CRUD basiques ici
class BdService {
  private db: SQLite.SQLiteDatabase | null = null;
  private signature: string;
  public TABLE_PAYS: string;
  public TABLE_UNIVERSITES: string;

  // Initialisation du service
  constructor() {
    this.signature = obtenirSignatureEtudiant();
    // J'ajoute "sig_" devant pour que le nom de table soit valide en SQL
    this.TABLE_PAYS = `sig_${this.signature}_pays`;
    this.TABLE_UNIVERSITES = `sig_${this.signature}_universites`;
  }

  // Fonction pour initialiser la connexion à la BD
  async init(): Promise<void> {
    if (this.db) return; // Déjà initialisée

    this.db = await SQLite.openDatabaseAsync('afriqueUniversites.db');
    await this.creerTables();
    console.log('BD prête, signature:', this.signature);
  }

  // Création des tables (si elles existent pas déjà)
  private async creerTables(): Promise<void> {
    if (!this.db) throw new Error('Database not initialized');

    // Table pour stocker les pays africains
    await this.db.execAsync(`
      CREATE TABLE IF NOT EXISTS ${this.TABLE_PAYS} (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        cca2 TEXT UNIQUE NOT NULL,
        nom_commun TEXT NOT NULL,
        nom_officiel TEXT,
        drapeau_url TEXT,
        population INTEGER,
        region TEXT,
        sous_region TEXT,
        date_creation TEXT DEFAULT CURRENT_TIMESTAMP,
        date_maj TEXT DEFAULT CURRENT_TIMESTAMP
      );
    `);

    // Créer la table des universités
    await this.db.execAsync(`
      CREATE TABLE IF NOT EXISTS ${this.TABLE_UNIVERSITES} (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        pays_cca2 TEXT NOT NULL,
        nom TEXT NOT NULL,
        site_web TEXT,
        domaines TEXT,
        date_creation TEXT DEFAULT CURRENT_TIMESTAMP,
        date_maj TEXT DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (pays_cca2) REFERENCES ${this.TABLE_PAYS}(cca2)
      );
    `);

    // Créer des index pour améliorer les performances
    await this.db.execAsync(`
      CREATE INDEX IF NOT EXISTS idx_pays_population ON ${this.TABLE_PAYS}(population);
      CREATE INDEX IF NOT EXISTS idx_universites_pays ON ${this.TABLE_UNIVERSITES}(pays_cca2);
    `);
  }

  /**
   * Obtenir l'instance de la base de données
   */
  private getDb(): SQLite.SQLiteDatabase {
    if (!this.db) {
      throw new Error('Database not initialized. Call init() first.');
    }
    return this.db;
  }

  // ==================== Opérations pour les pays ====================

  // Ajouter un nouveau pays dans la base
  async insererPays(pays: Pays): Promise<void> {
    const db = this.getDb();
    const maintenant = new Date().toISOString();

    await db.runAsync(
      `INSERT INTO ${this.TABLE_PAYS} 
       (cca2, nom_commun, nom_officiel, drapeau_url, population, region, sous_region, date_creation, date_maj)
       VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)`,
      [
        pays.cca2,
        pays.nom_commun,
        pays.nom_officiel || '',
        pays.drapeau_url || '',
        pays.population || 0,
        pays.region || '',
        pays.sous_region || '',
        maintenant,
        maintenant
      ]
    );
  }

  /**
   * ÉCRITURE - Mettre à jour un pays existant
   */
  async mettreAJourPays(pays: Pays): Promise<void> {
    const db = this.getDb();
    const dateMaj = new Date().toISOString();

    await db.runAsync(
      `UPDATE ${this.TABLE_PAYS} 
       SET nom_commun = ?, nom_officiel = ?, drapeau_url = ?, 
           population = ?, region = ?, sous_region = ?, date_maj = ?
       WHERE cca2 = ?`,
      [
        pays.nom_commun,
        pays.nom_officiel || '',
        pays.drapeau_url || '',
        pays.population || 0,
        pays.region || '',
        pays.sous_region || '',
        dateMaj,
        pays.cca2
      ]
    );
  }

  /**
   * ÉCRITURE - Insérer ou mettre à jour un pays (UPSERT)
   */
  async sauvegarderPays(pays: Pays): Promise<void> {
    const db = this.getDb();
    const dateMaj = new Date().toISOString();

    // Utiliser INSERT OR REPLACE pour éviter les problèmes de race condition
    await db.runAsync(
      `INSERT OR REPLACE INTO ${this.TABLE_PAYS} 
       (cca2, nom_commun, nom_officiel, drapeau_url, population, region, sous_region, date_maj)
       VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
      [
        pays.cca2,
        pays.nom_commun,
        pays.nom_officiel || '',
        pays.drapeau_url || '',
        pays.population || 0,
        pays.region || '',
        pays.sous_region || '',
        dateMaj
      ]
    );
  }

  /**
   * LECTURE - Récupérer tous les pays avec filtres optionnels
   */
  async lirePays(
    searchQuery?: string,
    minPopulation?: number,
    maxPopulation?: number
  ): Promise<Pays[]> {
    const db = this.getDb();
    
    let query = `SELECT * FROM ${this.TABLE_PAYS} WHERE 1=1`;
    const params: any[] = [];

    // Filtre de recherche
    if (searchQuery && searchQuery.trim() !== '') {
      query += ` AND (nom_commun LIKE ? OR nom_officiel LIKE ?)`;
      const searchPattern = `%${searchQuery}%`;
      params.push(searchPattern, searchPattern);
    }

    // Filtre de population minimale
    if (minPopulation !== undefined && minPopulation > 0) {
      query += ` AND population >= ?`;
      params.push(minPopulation);
    }

    // Filtre de population maximale
    if (maxPopulation !== undefined && maxPopulation > 0) {
      query += ` AND population <= ?`;
      params.push(maxPopulation);
    }

    // Trier par population croissante
    query += ` ORDER BY population ASC`;

    const pays = await db.getAllAsync<Pays>(query, params);
    return pays;
  }

  /**
   * LECTURE - Récupérer un pays par son code
   */
  async lirePaysParCca2(cca2: string): Promise<Pays | null> {
    const db = this.getDb();
    
    const pays = await db.getFirstAsync<Pays>(
      `SELECT * FROM ${this.TABLE_PAYS} WHERE cca2 = ?`,
      [cca2]
    );
    
    return pays || null;
  }

  /**
   * SUPPRESSION - Supprimer un pays
   */
  async supprimerPays(cca2: string): Promise<void> {
    const db = this.getDb();
    
    // Supprimer d'abord les universités associées
    await db.runAsync(
      `DELETE FROM ${this.TABLE_UNIVERSITES} WHERE pays_cca2 = ?`,
      [cca2]
    );
    
    // Puis supprimer le pays
    await db.runAsync(
      `DELETE FROM ${this.TABLE_PAYS} WHERE cca2 = ?`,
      [cca2]
    );
  }

  /**
   * SUPPRESSION - Supprimer tous les pays
   */
  async supprimerTousLesPays(): Promise<void> {
    const db = this.getDb();
    
    await db.runAsync(`DELETE FROM ${this.TABLE_UNIVERSITES}`);
    await db.runAsync(`DELETE FROM ${this.TABLE_PAYS}`);
  }

  // ==================== OPÉRATIONS CRUD POUR LES UNIVERSITÉS ====================

  /**
   * ÉCRITURE - Insérer une nouvelle université
   */
  async insererUniversite(universite: Universite): Promise<void> {
    const db = this.getDb();
    const dateNow = new Date().toISOString();

    await db.runAsync(
      `INSERT INTO ${this.TABLE_UNIVERSITES} 
       (pays_cca2, nom, site_web, domaines, date_creation, date_maj)
       VALUES (?, ?, ?, ?, ?, ?)`,
      [
        universite.pays_cca2,
        universite.nom,
        universite.site_web || '',
        universite.domaines || '',
        dateNow,
        dateNow
      ]
    );
  }

  /**
   * ÉCRITURE - Remplacer toutes les universités d'un pays
   */
  async remplacerUniversitesPays(countryCca2: string, universites: Universite[]): Promise<void> {
    const db = this.getDb();
    const dateNow = new Date().toISOString();

    // Supprimer les anciennes universités
    await db.runAsync(
      `DELETE FROM ${this.TABLE_UNIVERSITES} WHERE pays_cca2 = ?`,
      [countryCca2]
    );

    // Insérer les nouvelles universités
    for (const universite of universites) {
      await db.runAsync(
        `INSERT INTO ${this.TABLE_UNIVERSITES} 
         (pays_cca2, nom, site_web, domaines, date_creation, date_maj)
         VALUES (?, ?, ?, ?, ?, ?)`,
        [
          countryCca2,
          universite.nom,
          universite.site_web || '',
          universite.domaines || '',
          dateNow,
          dateNow
        ]
      );
    }
  }

  /**
   * LECTURE - Récupérer les universités d'un pays
   */
  async lireUniversites(countryCca2: string): Promise<Universite[]> {
    const db = this.getDb();
    
    const universites = await db.getAllAsync<Universite>(
      `SELECT * FROM ${this.TABLE_UNIVERSITES} WHERE pays_cca2 = ? ORDER BY nom ASC`,
      [countryCca2]
    );
    
    return universites;
  }

  /**
   * SUPPRESSION - Supprimer une université
   */
  async supprimerUniversite(id: number): Promise<void> {
    const db = this.getDb();
    
    await db.runAsync(
      `DELETE FROM ${this.TABLE_UNIVERSITES} WHERE id = ?`,
      [id]
    );
  }

  /**
   * SUPPRESSION - Supprimer toutes les universités d'un pays
   */
  async supprimerUniversitesPays(countryCca2: string): Promise<void> {
    const db = this.getDb();
    
    await db.runAsync(
      `DELETE FROM ${this.TABLE_UNIVERSITES} WHERE pays_cca2 = ?`,
      [countryCca2]
    );
  }

  // ==================== UTILITAIRES ====================

  /**
   * Vérifier si des données existent en local
   */
  async aDonnees(): Promise<boolean> {
    const db = this.getDb();
    
    const result = await db.getFirstAsync<{ count: number }>(
      `SELECT COUNT(*) as count FROM ${this.TABLE_PAYS}`
    );
    
    return (result?.count ?? 0) > 0;
  }

  /**
   * Obtenir la signature utilisée
   */
  getSignature(): string {
    return this.signature;
  }
}

// Exporter une instance unique (Singleton)
export default new BdService();
