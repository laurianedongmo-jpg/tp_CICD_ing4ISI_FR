/**
 * Interface pour les données d'une université
 */
export interface Universite {
  id?: number;
  pays_cca2: string;
  nom: string;
  site_web?: string;
  domaines?: string;
  date_creation?: string;
  date_maj?: string;
}

/**
 * Interface pour les universités de l'API Hipolabs
 */
export interface UniversityAPI {
  name: string;
  country: string;
  alpha_two_code: string;
  web_pages: string[];
  domains: string[];
}
