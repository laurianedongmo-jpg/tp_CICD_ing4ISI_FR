/**
 * Service API pour récupérer les données depuis les APIs externes
 */

const REST_COUNTRIES_API = 'https://restcountries.com/v3.1';
const UNIVERSITIES_API = 'http://universities.hipolabs.com';

/**
 * Récupérer tous les pays africains depuis l'API REST Countries
 */
export async function getAfricanCountries(): Promise<any[]> {
  try {
    const response = await fetch(`${REST_COUNTRIES_API}/region/africa`);
    
    if (!response.ok) {
      throw new Error(`Erreur HTTP: ${response.status}`);
    }
    
    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Erreur lors de la récupération des pays africains:', error);
    throw error;
  }
}

/**
 * Récupérer les universités d'un pays depuis l'API Universities
 */
export async function getUniversitiesByCountry(countryName: string): Promise<any[]> {
  try {
    const response = await fetch(
      `${UNIVERSITIES_API}/search?country=${encodeURIComponent(countryName)}`
    );
    
    if (!response.ok) {
      throw new Error(`Erreur HTTP: ${response.status}`);
    }
    
    const data = await response.json();
    return data;
  } catch (error) {
    console.error(`Erreur lors de la récupération des universités pour ${countryName}:`, error);
    throw error;
  }
}
