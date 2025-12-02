import * as Network from 'expo-network';
import { Pays } from '../interfaces/Pays.interface';
import { Universite } from '../interfaces/Universite.interface';
import { getAfricanCountries, getUniversitiesByCountry } from './api.service';
import bdService from './bd.service';

// Service de synchro entre l'API et la BD locale

// Vérifier si on a Internet
export async function isConnected(): Promise<boolean> {
  const networkState = await Network.getNetworkStateAsync();
  return networkState.isConnected ?? false;
}

// Écouter les changements de connexion
export function subscribeToConnectionChanges(
  callback: (isConnected: boolean) => void
): () => void {
  let previousState: boolean | null = null;
  
  // Vérifier périodiquement la connexion (expo-network n'a pas d'addEventListener)
  const interval = setInterval(async () => {
    const networkState = await Network.getNetworkStateAsync();
    const currentState = networkState.isConnected ?? false;
    
    // Appeler le callback seulement si l'état a changé
    if (previousState !== currentState) {
      previousState = currentState;
      callback(currentState);
    }
  }, 3000); // Vérifier toutes les 3 secondes
  
  return () => clearInterval(interval);
}

// Charger les pays depuis l'API et les sauvegarder en local
export async function loadCountriesFromServer(): Promise<void> {
  const connected = await isConnected();
  
  if (!connected) {
    console.log('Pas de connexion, on utilise les données locales');
    return;
  }

  try {
    const countries = await getAfricanCountries();

    // Parcourir et sauvegarder chaque pays
    for (const country of countries) {
      const pays: Pays = {
        cca2: country.cca2,
        nom_commun: country.name.common,
        nom_officiel: country.name.official,
        drapeau_url: country.flags.png,
        population: country.population,
        region: country.region,
        sous_region: country.subregion || ''
      };

      await bdService.sauvegarderPays(pays);
    }

    console.log(`${countries.length} pays chargés et sauvegardés`);
  } catch (error) {
    console.error('Erreur pendant la synchro:', error);
    throw error;
  }
}

// Charger les universités d'un pays depuis le serveur
export async function loadUniversitiesFromServer(countryName: string, countryCca2: string): Promise<void> {
  const connected = await isConnected();
  
  if (!connected) {
    console.log('Pas de connexion, on utilise le cache local');
    return;
  }

  try {
    const universities = await getUniversitiesByCountry(countryName);

    const universites: Universite[] = universities.map((uni: any) => ({
      pays_cca2: countryCca2,
      nom: uni.name,
      site_web: uni.web_pages[0] || '',
      domaines: JSON.stringify(uni.domains)
    }));

    // Remplacer les universités du pays
    await bdService.remplacerUniversitesPays(countryCca2, universites);

    console.log(`${universities.length} universités chargées pour ${countryName}`);
  } catch (error) {
    console.error(`Erreur lors de la synchronisation des universités pour ${countryName}:`, error);
    throw error;
  }
}

// Récupérer les pays depuis la BD locale
export async function getLocalCountries(
  searchQuery?: string,
  minPopulation?: number,
  maxPopulation?: number
): Promise<Pays[]> {
  return await bdService.lirePays(searchQuery, minPopulation, maxPopulation);
}

// Récupérer les universités d'un pays depuis la BD locale
export async function getLocalUniversities(countryCca2: string): Promise<Universite[]> {
  return await bdService.lireUniversites(countryCca2);
}

// Vérifier si on a des données en local
export async function hasLocalData(): Promise<boolean> {
  return await bdService.aDonnees();
}

// ==================== HANDLE REQUEST - PATTERN DU COURS ====================

// Méthode centralisée pour gérer les requêtes avec gestion online/offline
export async function handleRequest<T>(
  loadFromServer: () => Promise<T>,
  loadFromStorage: () => Promise<T>
): Promise<T> {
  const online = await isConnected();
  
  if (!online) {
    console.log('Pas de connexion, je charge depuis la BD locale');
    return await loadFromStorage();
  } else {
    console.log('Connexion OK, je charge depuis le serveur');
    try {
      const data = await loadFromServer();
      return data;
    } catch (error) {
      // Fallback sur le storage si erreur serveur
      console.error('Erreur serveur, fallback vers storage local:', error);
      return await loadFromStorage();
    }
  }
}

// Charger les pays avec gestion automatique online/offline
export async function loadCountries(
  searchQuery?: string,
  minPopulation?: number,
  maxPopulation?: number
): Promise<Pays[]> {
  return await handleRequest(
    async () => {
      await loadCountriesFromServer();
      return await bdService.lirePays(searchQuery, minPopulation, maxPopulation);
    },
    async () => {
      return await bdService.lirePays(searchQuery, minPopulation, maxPopulation);
    }
  );
}

// Charger les universités avec gestion automatique online/offline
export async function loadUniversities(
  countryName: string,
  countryCca2: string
): Promise<Universite[]> {
  return await handleRequest(
    async () => {
      await loadUniversitiesFromServer(countryName, countryCca2);
      return await bdService.lireUniversites(countryCca2);
    },
    async () => {
      return await bdService.lireUniversites(countryCca2);
    }
  );
}
