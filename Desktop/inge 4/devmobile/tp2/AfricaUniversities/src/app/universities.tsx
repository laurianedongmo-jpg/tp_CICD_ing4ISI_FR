import * as Linking from 'expo-linking';
import { useLocalSearchParams, useRouter } from 'expo-router';
import React, { useEffect, useState } from 'react';
import {
    Alert,
    FlatList,
    StyleSheet,
    Text,
    TouchableOpacity,
    View
} from 'react-native';
import ActivityLoading from '../components/ActivityLoading';
import { Pays } from '../interfaces/Pays.interface';
import { Universite } from '../interfaces/Universite.interface';
import {
    isConnected,
    loadUniversities as loadUniversitiesFromService,
    subscribeToConnectionChanges
} from '../services/sync.service';

/**
 * Page des universités d'un pays
 */
export default function Universities() {
  const params = useLocalSearchParams();
  const router = useRouter();
  const [universities, setUniversities] = useState<Universite[]>([]);
  const [loading, setLoading] = useState(true);
  const [connected, setConnected] = useState(false);

  // Récupérer les paramètres individuels au lieu d'un JSON
  const country: Pays | null = params.cca2 ? {
    cca2: params.cca2 as string,
    nom_commun: params.nom as string,
    drapeau_url: params.drapeau as string
  } : null;

  useEffect(() => {
    if (!country) {
      Alert.alert('Erreur', 'Pays non trouvé');
      router.back();
      return;
    }

    loadUniversitiesData();

    // Écouter automatiquement les changements de connexion
    const unsubscribe = subscribeToConnectionChanges((isOnline) => {
      const wasOffline = !connected;
      setConnected(isOnline);
      
      // Recharger seulement si on revient en ligne après avoir été hors ligne
      if (isOnline && wasOffline && country && universities.length > 0) {
        console.log('Connexion rétablie, je recharge les universités');
        loadUniversitiesData();
      }
    });

    // Nettoyer l'écouteur au démontage
    return () => unsubscribe();
  }, []);

  const loadUniversitiesData = async () => {
    if (!country) return;

    try {
      setLoading(true);

      // Vérifier la connexion pour l'affichage
      const isOnline = await isConnected();
      setConnected(isOnline);

      // Utiliser handleRequest pattern du cours
      const universitiesData = await loadUniversitiesFromService(country.nom_commun, country.cca2);
      setUniversities(universitiesData);
    } catch (error) {
      console.error('Erreur lors du chargement des universités:', error);
      Alert.alert('Erreur', 'Impossible de charger les universités');
    } finally {
      setLoading(false);
    }
  };

  const openWebsite = (url: string) => {
    if (url) {
      Linking.openURL(url).catch(() => {
        Alert.alert('Erreur', 'Impossible d\'ouvrir le lien');
      });
    }
  };

  const renderUniversityItem = ({ item }: { item: Universite }) => (
    <TouchableOpacity
      style={styles.universityItem}
      onPress={() => item.site_web && openWebsite(item.site_web)}
    >
      <Text style={styles.universityName}>{item.nom}</Text>
      {item.site_web && (
        <Text style={styles.website} numberOfLines={1}>
          🌐 {item.site_web}
        </Text>
      )}
    </TouchableOpacity>
  );

  if (loading) {
    return <ActivityLoading message="Chargement des universités..." />;
  }

  return (
    <View style={styles.container}>
      <View style={[styles.connectionBanner, connected ? styles.online : styles.offline]}>
        <Text style={styles.connectionText}>
          {connected ? '🟢 En ligne' : '🔴 Hors ligne'}
        </Text>
      </View>

      <View style={styles.header}>
        <Text style={styles.countryName}>{country?.nom_commun}</Text>
        <Text style={styles.universityCount}>
          {universities.length} université{universities.length > 1 ? 's' : ''}
        </Text>
      </View>

      <FlatList
        data={universities}
        renderItem={renderUniversityItem}
        keyExtractor={(item, index) => `${item.id || index}`}
        ListEmptyComponent={
          <View style={styles.emptyContainer}>
            <Text style={styles.emptyText}>
              Aucune université trouvée pour ce pays
            </Text>
          </View>
        }
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  connectionBanner: {
    padding: 8,
    alignItems: 'center',
  },
  online: {
    backgroundColor: '#d4edda',
  },
  offline: {
    backgroundColor: '#f8d7da',
  },
  connectionText: {
    fontSize: 14,
    fontWeight: '600',
  },
  header: {
    padding: 16,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#ddd',
  },
  countryName: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
  },
  universityCount: {
    fontSize: 14,
    color: '#666',
    marginTop: 4,
  },
  universityItem: {
    padding: 16,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
  },
  universityName: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginBottom: 4,
  },
  website: {
    fontSize: 14,
    color: '#007AFF',
  },
  emptyContainer: {
    padding: 40,
    alignItems: 'center',
  },
  emptyText: {
    fontSize: 16,
    color: '#999',
    textAlign: 'center',
  },
});
