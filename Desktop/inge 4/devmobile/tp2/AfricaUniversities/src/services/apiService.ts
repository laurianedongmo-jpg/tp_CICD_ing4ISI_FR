import axios from 'axios';

const REST_COUNTRIES_API = 'https://restcountries.com/v3.1';
const UNIVERSITIES_API = 'http://universities.hipolabs.com';

/**
 * Récupérer les pays africains depuis l'API RESTCountries
 */
export const fetchAfricanCountries = async (): Promise<any[]> => {
  try {
    const response = await axios.get(`${REST_COUNTRIES_API}/region/africa`);
    return response.data;
  } catch (error) {
    console.error('Erreur lors de la récupération des pays africains:', error);
    throw error;
  }
};

/**
 * Récupérer les universités d'un pays depuis l'API Hipolabs
 */
export const fetchUniversitiesByCountry = async (countryName: string): Promise<any[]> => {
  try {
    const response = await axios.get(`${UNIVERSITIES_API}/search`, {
      params: { country: countryName }
    });
    return response.data;
  } catch (error) {
    console.error(`Erreur lors de la récupération des universités pour ${countryName}:`, error);
    throw error;
  }
};
