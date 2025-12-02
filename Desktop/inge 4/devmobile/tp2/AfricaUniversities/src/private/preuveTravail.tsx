/**
 * Fonction cryptographique personnalisée
 * 
 * Cette fonction génère une signature unique basée sur :
 * - Le nom de l'étudiant
 * - Le matricule de l'étudiant
 * - Un code secret fourni
 * 
 * Processus de génération :
 * 1. Concaténation des trois éléments (nom + matricule + code secret)
 * 2. Permutation : Inversion de la chaîne et mélange par blocs
 * 3. Opération arithmétique : Calcul de checksum avec modulo et multiplication
 * 4. Conversion en hexadécimal
 * 
 * @param nom - Nom de l'étudiant
 * @param matricule - Matricule de l'étudiant
 * @param codeSecret - Code secret fourni par l'instructeur
 * @returns Signature en format hexadécimal
 */
export const genererSignature = (
  nom: string,
  matricule: string,
  codeSecret: string
): string => {
  // Étape 1 : Concaténation
  const concatenation = `${nom}${matricule}${codeSecret}`;
  
  // Étape 2 : Permutation - Inversion de la chaîne
  const inverse = concatenation.split('').reverse().join('');
  
  // Permutation supplémentaire : Mélange par blocs de 3 caractères
  let melange = '';
  for (let i = 0; i < inverse.length; i += 3) {
    const bloc = inverse.substring(i, i + 3);
    // Inverser chaque bloc
    melange += bloc.split('').reverse().join('');
  }
  
  // Étape 3 : Opération arithmétique
  // Calculer un checksum basé sur les codes ASCII
  let checksum = 0;
  for (let i = 0; i < melange.length; i++) {
    const code = melange.charCodeAt(i);
    // Multiplication par la position + 1, puis modulo 256
    checksum += (code * (i + 1)) % 256;
  }
  
  // Ajouter une opération supplémentaire avec le checksum
  const checksumFinal = (checksum * 17 + 42) % 65536;
  
  // Étape 4 : Conversion en hexadécimal
  // Combiner le melange avec le checksum pour créer la signature finale
  let signatureNumerique = checksumFinal;
  for (let i = 0; i < Math.min(melange.length, 12); i++) {
    signatureNumerique += melange.charCodeAt(i) * (i + 1);
  }
  
  // Convertir en hexadécimal et s'assurer d'avoir au moins 8 caractères
  let hex = signatureNumerique.toString(16).toUpperCase();
  
  // S'assurer que la signature fait au moins 8 caractères
  if (hex.length < 8) {
    // Ajouter des caractères basés sur le nom et matricule
    const extra = (nom.length * matricule.length * 31).toString(16).toUpperCase();
    hex = hex + extra;
  }
  
  // Limiter à 8 caractères pour garder une taille raisonnable
  return hex.substring(0, 8);
};

/**
 * Fonction pour obtenir le préfixe des tables SQLite
 * Utilise la signature générée comme préfixe
 * Ajoute un préfixe alphabétique car SQLite n'accepte pas les noms commençant par un chiffre
 */
export const obtenirPrefixeTable = (
  nom: string,
  matricule: string,
  codeSecret: string
): string => {
  const signature = genererSignature(nom, matricule, codeSecret);
  return `T${signature}_`;
};

/**
 * Fonction d'initialisation à appeler au démarrage de l'application
 * Affiche la signature dans la console pour vérification
 */
export const initialiserPreuveTravail = (
  nom: string,
  matricule: string,
  codeSecret: string
): void => {
  const signature = genererSignature(nom, matricule, codeSecret);
  console.log('=================================');
  console.log('PREUVE DE TRAVAIL - SIGNATURE');
  console.log('=================================');
  console.log(`Nom: ${nom}`);
  console.log(`Matricule: ${matricule}`);
  console.log(`Signature: ${signature}`);
  console.log('=================================');
};

/**
 * Fonction pour obtenir la signature de l'étudiant
 * Utilisée par le service de base de données
 */
export const obtenirSignatureEtudiant = (): string => {
  const { nom, matricule, codeSecret } = require('./config').CONFIG_ETUDIANT;
  return genererSignature(nom, matricule, codeSecret);
};
