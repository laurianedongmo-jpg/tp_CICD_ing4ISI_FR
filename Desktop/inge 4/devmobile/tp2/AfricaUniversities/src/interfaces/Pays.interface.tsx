/**
 * Interface pour les données d'un pays
 */
export interface Pays {
  id?: number;
  cca2: string;
  nom_commun: string;
  nom_officiel?: string;
  drapeau_url?: string;
  population?: number;
  region?: string;
  sous_region?: string;
  date_creation?: string;
  date_maj?: string;
}

/**
 * Interface pour les pays de l'API REST Countries
 */
export interface CountryAPI {
  name: {
    common: string;
    official: string;
  };
  cca2: string;
  flags: {
    png: string;
    svg: string;
  };
  population: number;
  region: string;
  subregion?: string;
}
