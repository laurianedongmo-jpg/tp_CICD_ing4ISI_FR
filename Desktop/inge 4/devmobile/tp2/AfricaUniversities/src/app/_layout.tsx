import { Stack } from 'expo-router';
import React, { useEffect, useState } from 'react';
import ActivityLoading from '../components/ActivityLoading';
import bdService from '../services/bd.service';
import { initialiserPreuveTravail } from '../private/preuveTravail';
import { CONFIG_ETUDIANT } from '../private/config';

/**
 * Layout principal de l'application
 * Initialise la base de données avant de rendre l'application
 */
export default function RootLayout() {
  const [isReady, setIsReady] = useState(false);

  useEffect(() => {
    async function initializeApp() {
      try {
        // Initialiser la preuve de travail
        initialiserPreuveTravail(
          CONFIG_ETUDIANT.nom,
          CONFIG_ETUDIANT.matricule,
          CONFIG_ETUDIANT.codeSecret
        );
        
        // Initialiser la base de données
        await bdService.init();
        console.log('App prête !');
        setIsReady(true);
      } catch (error) {
        console.error('Erreur lors de l\'initialisation:', error);
        setIsReady(true);
      }
    }

    initializeApp();
  }, []);

  if (!isReady) {
    return <ActivityLoading message="Initialisation..." />;
  }

  return (
    <Stack
      screenOptions={{
        headerStyle: { backgroundColor: '#007AFF' },
        headerTintColor: '#fff',
        headerTitleStyle: { fontWeight: 'bold' },
      }}
    >
      <Stack.Screen 
        name="index" 
        options={{ title: 'Pays Africains' }} 
      />
      <Stack.Screen 
        name="universities" 
        options={{ title: 'Universités' }} 
      />
    </Stack>
  );
}
