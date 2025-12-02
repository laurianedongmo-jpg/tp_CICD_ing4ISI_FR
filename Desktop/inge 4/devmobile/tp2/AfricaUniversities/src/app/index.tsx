import { FlashList } from '@shopify/flash-list';
import { useRouter } from 'expo-router';
import React, { useCallback, useEffect, useState } from 'react';
import {
    Alert,
    Image,
    Modal,
    StyleSheet,
    Text,
    TextInput,
    TouchableOpacity,
    View
} from 'react-native';
import { Pays } from '../interfaces/Pays.interface';
import {
    isConnected,
    loadCountries,
    subscribeToConnectionChanges
} from '../services/sync.service';

// Page d'accueil avec la liste des pays africains
type SortOrder = 'none' | 'asc' | 'desc';

export default function Index() {
  const router = useRouter();
  const [countries, setCountries] = useState<Pays[]>([]);
  const [allCountries, setAllCountries] = useState<Pays[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [showFilterModal, setShowFilterModal] = useState(false);
  const [filterPopulation15M, setFilterPopulation15M] = useState(false);
  const [sortOrder, setSortOrder] = useState<SortOrder>('none');
  const [connected, setConnected] = useState(false);

  useEffect(() => {
    loadData();

    // J'écoute les changements de connexion
    const unsubscribe = subscribeToConnectionChanges((isOnline) => {
      const wasOffline = !connected;
      setConnected(isOnline);
      
      // Recharger seulement si on revient en ligne après avoir été hors ligne
      if (isOnline && wasOffline && countries.length > 0) {
        console.log('Connexion rétablie, je recharge les données');
        loadData();
      }
    });

    // Nettoyage quand le composant se démonte
    return () => unsubscribe();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      
      // Je vérifie d'abord si on a Internet
      const isOnline = await isConnected();
      setConnected(isOnline);

      // Chargement des pays
      const countries = await loadCountries();
      
      if (countries.length === 0 && !isOnline) {
        Alert.alert(
          'Pas de connexion',
          'Aucune donnée locale disponible. Veuillez vous connecter à Internet.'
        );
      }

      setCountries(countries);
      setAllCountries(countries);
    } catch (error) {
      console.error('Erreur lors du chargement des données:', error);
      Alert.alert('Erreur', 'Impossible de charger les données');
    } finally {
      setLoading(false);
    }
  };

  const applyAllFilters = useCallback(() => {
    let filtered = [...allCountries];

    // Recherche par nom
    if (searchQuery.trim() !== '') {
      filtered = filtered.filter(country =>
        country.nom_commun.toLowerCase().includes(searchQuery.toLowerCase()) ||
        (country.nom_officiel?.toLowerCase().includes(searchQuery.toLowerCase()) ?? false)
      );
    }

    // Filtre pour les pays avec plus de 15M habitants
    if (filterPopulation15M) {
      filtered = filtered.filter(country => (country.population ?? 0) > 15000000);
    }

    // Tri alphabétique
    if (sortOrder === 'asc') {
      filtered.sort((a, b) => a.nom_commun.localeCompare(b.nom_commun));
    } else if (sortOrder === 'desc') {
      filtered.sort((a, b) => b.nom_commun.localeCompare(a.nom_commun));
    }

    setCountries(filtered);
  }, [allCountries, searchQuery, filterPopulation15M, sortOrder]);

  const handleSearch = useCallback((text: string) => {
    setSearchQuery(text);
  }, []);

  // Appliquer les filtres quand quelque chose change
  useEffect(() => {
    if (allCountries.length > 0) {
      applyAllFilters();
    }
  }, [searchQuery, filterPopulation15M, sortOrder, allCountries]);

  const applyFiltersFromModal = () => {
    setShowFilterModal(false);
    applyAllFilters();
  };

  const resetFilters = () => {
    setFilterPopulation15M(false);
    setSortOrder('none');
    setSearchQuery('');
    setShowFilterModal(false);
  };

  const renderCountryItem = ({ item }: { item: Pays }) => {
    if (!item) return null;
    
    const countryName = item.nom_commun ? String(item.nom_commun) : 'Unknown';
    const population = item.population ? Number(item.population) : null;
    
    return (
      <TouchableOpacity
        style={styles.countryItem}
        onPress={() => router.push({
          pathname: '/universities',
          params: { 
            cca2: item.cca2,
            nom: item.nom_commun,
            drapeau: item.drapeau_url || ''
          }
        })}
      >
        <Image
          source={{ uri: item.drapeau_url || '' }}
          style={styles.flag}
          resizeMode="contain"
        />
        <View style={styles.countryInfo}>
          <Text style={styles.countryName}>{countryName}</Text>
          {population && (
            <Text style={styles.population}>
              Population: {population.toLocaleString()}
            </Text>
          )}
        </View>
      </TouchableOpacity>
    );
  };

  // Skeleton loader
  const renderSkeleton = () => (
    <View style={styles.container}>
      <View style={styles.connectionBanner}>
        <View style={styles.skeletonText} />
      </View>
      <View style={styles.searchContainer}>
        <View style={[styles.searchInput, styles.skeletonInput]} />
        <View style={[styles.filterButton, styles.skeletonButton]} />
      </View>
      {[1, 2, 3, 4, 5, 6].map((i) => (
        <View key={i} style={styles.skeletonItem}>
          <View style={styles.skeletonFlag} />
          <View style={styles.skeletonInfo}>
            <View style={styles.skeletonTextLarge} />
            <View style={styles.skeletonTextSmall} />
          </View>
        </View>
      ))}
    </View>
  );

  if (loading && countries.length === 0) {
    return renderSkeleton();
  }

  return (
    <View style={styles.container}>
      <View style={[styles.connectionBanner, connected ? styles.online : styles.offline]}>
        <Text style={styles.connectionText}>
          {connected ? '🟢 En ligne' : '🔴 Hors ligne'}
        </Text>
        <TouchableOpacity
          style={styles.proofButton}
          onPress={() => router.push('/preuveTravail')}
        >
          <Text style={styles.offlineButtonText}>🔐 Signature</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.searchContainer}>
        <TextInput
          style={styles.searchInput}
          placeholder="Rechercher un pays..."
          value={searchQuery}
          onChangeText={handleSearch}
        />
        <TouchableOpacity
          style={styles.filterButton}
          onPress={() => setShowFilterModal(true)}
        >
          <Text style={styles.filterButtonText}>⚙</Text>
        </TouchableOpacity>
      </View>

      <FlashList
        data={countries}
        renderItem={renderCountryItem}
        keyExtractor={(item) => item.cca2}
        ListEmptyComponent={
          <View style={styles.emptyContainer}>
            <Text style={styles.emptyText}>Aucun pays trouvé</Text>
          </View>
        }
      />

      <Modal
        visible={showFilterModal}
        transparent
        animationType="slide"
        onRequestClose={() => setShowFilterModal(false)}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.modalContent}>
            <Text style={styles.modalTitle}>🔍 Filtres et Tri</Text>
            
            {/* Tri alphabétique */}
            <Text style={styles.sectionTitle}>📊 Tri Alphabétique</Text>
            <View style={styles.optionsContainer}>
              <TouchableOpacity
                style={[
                  styles.optionButton,
                  sortOrder === 'asc' && styles.optionButtonActive
                ]}
                onPress={() => setSortOrder(sortOrder === 'asc' ? 'none' : 'asc')}
              >
                <Text style={[
                  styles.optionText,
                  sortOrder === 'asc' && styles.optionTextActive
                ]}>
                  ↓ A → Z
                </Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={[
                  styles.optionButton,
                  sortOrder === 'desc' && styles.optionButtonActive
                ]}
                onPress={() => setSortOrder(sortOrder === 'desc' ? 'none' : 'desc')}
              >
                <Text style={[
                  styles.optionText,
                  sortOrder === 'desc' && styles.optionTextActive
                ]}>
                  ↑ Z → A
                </Text>
              </TouchableOpacity>
            </View>

            {/* Filtre population */}
            <Text style={styles.sectionTitle}>👥 Filtre Population</Text>
            <TouchableOpacity
              style={[
                styles.filterOptionButton,
                filterPopulation15M && styles.filterOptionButtonActive
              ]}
              onPress={() => setFilterPopulation15M(!filterPopulation15M)}
            >
              <Text style={[
                styles.filterOptionText,
                filterPopulation15M && styles.filterOptionTextActive
              ]}>
                {filterPopulation15M ? '✓ ' : ''}Population {'>'} 15 Millions
              </Text>
            </TouchableOpacity>

            {/* Boutons d'action */}
            <View style={styles.modalButtons}>
              <TouchableOpacity
                style={[styles.button, styles.resetButton]}
                onPress={resetFilters}
              >
                <Text style={styles.buttonText}>Réinitialiser</Text>
              </TouchableOpacity>
              
              <TouchableOpacity
                style={[styles.button, styles.applyButton]}
                onPress={applyFiltersFromModal}
              >
                <Text style={styles.buttonText}>Appliquer</Text>
              </TouchableOpacity>
            </View>

            <TouchableOpacity
              style={styles.closeButton}
              onPress={() => setShowFilterModal(false)}
            >
              <Text style={styles.closeButtonText}>Fermer</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
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
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 16,
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
  proofButton: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    backgroundColor: '#27ae60',
    borderRadius: 6,
  },
  offlineButtonText: {
    color: '#fff',
    fontSize: 12,
    fontWeight: '600',
  },
  searchContainer: {
    flexDirection: 'row',
    padding: 10,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#ddd',
  },
  searchInput: {
    flex: 1,
    height: 40,
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    paddingHorizontal: 12,
    backgroundColor: '#f9f9f9',
  },
  filterButton: {
    marginLeft: 10,
    paddingHorizontal: 15,
    justifyContent: 'center',
    backgroundColor: '#007AFF',
    borderRadius: 8,
  },
  filterButtonText: {
    color: '#fff',
    fontWeight: '600',
  },
  countryItem: {
    flexDirection: 'row',
    padding: 15,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
    alignItems: 'center',
  },
  flag: {
    width: 60,
    height: 40,
    marginRight: 15,
    borderRadius: 4,
  },
  countryInfo: {
    flex: 1,
  },
  countryName: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
  },
  population: {
    fontSize: 14,
    color: '#666',
    marginTop: 4,
  },
  emptyContainer: {
    padding: 40,
    alignItems: 'center',
  },
  emptyText: {
    fontSize: 16,
    color: '#999',
  },
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.5)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  modalContent: {
    width: '85%',
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 20,
  },
  modalTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 20,
    textAlign: 'center',
  },
  label: {
    fontSize: 14,
    fontWeight: '600',
    marginTop: 10,
    marginBottom: 5,
    color: '#333',
  },
  input: {
    height: 40,
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    paddingHorizontal: 12,
    backgroundColor: '#f9f9f9',
  },
  modalButtons: {
    flexDirection: 'row',
    marginTop: 20,
    gap: 10,
  },
  button: {
    flex: 1,
    padding: 12,
    borderRadius: 8,
    alignItems: 'center',
  },
  resetButton: {
    backgroundColor: '#6c757d',
  },
  applyButton: {
    backgroundColor: '#007AFF',
  },
  buttonText: {
    color: '#fff',
    fontWeight: '600',
    fontSize: 16,
  },
  closeButton: {
    marginTop: 10,
    padding: 10,
    alignItems: 'center',
  },
  closeButtonText: {
    color: '#666',
    fontSize: 14,
  },
  // Skeleton loader styles
  skeletonText: {
    width: 100,
    height: 14,
    backgroundColor: '#e0e0e0',
    borderRadius: 4,
  },
  skeletonInput: {
    backgroundColor: '#e0e0e0',
  },
  skeletonButton: {
    backgroundColor: '#e0e0e0',
  },
  skeletonItem: {
    flexDirection: 'row',
    padding: 15,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
    alignItems: 'center',
  },
  skeletonFlag: {
    width: 60,
    height: 40,
    marginRight: 15,
    backgroundColor: '#e0e0e0',
    borderRadius: 4,
  },
  skeletonInfo: {
    flex: 1,
  },
  skeletonTextLarge: {
    width: '70%',
    height: 16,
    backgroundColor: '#e0e0e0',
    borderRadius: 4,
    marginBottom: 8,
  },
  skeletonTextSmall: {
    width: '40%',
    height: 14,
    backgroundColor: '#e0e0e0',
    borderRadius: 4,
  },
  // Modal filter styles
  sectionTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#333',
    marginTop: 15,
    marginBottom: 10,
  },
  optionsContainer: {
    flexDirection: 'row',
    gap: 10,
    marginBottom: 10,
  },
  optionButton: {
    flex: 1,
    paddingVertical: 12,
    paddingHorizontal: 16,
    borderRadius: 8,
    backgroundColor: '#f0f0f0',
    borderWidth: 2,
    borderColor: '#ddd',
    alignItems: 'center',
  },
  optionButtonActive: {
    backgroundColor: '#007AFF',
    borderColor: '#0056b3',
  },
  optionText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#666',
  },
  optionTextActive: {
    color: '#fff',
  },
  filterOptionButton: {
    paddingVertical: 14,
    paddingHorizontal: 16,
    borderRadius: 8,
    backgroundColor: '#f0f0f0',
    borderWidth: 2,
    borderColor: '#ddd',
    marginBottom: 10,
  },
  filterOptionButtonActive: {
    backgroundColor: '#27ae60',
    borderColor: '#1e8449',
  },
  filterOptionText: {
    fontSize: 15,
    fontWeight: '600',
    color: '#666',
    textAlign: 'center',
  },
  filterOptionTextActive: {
    color: '#fff',
  },
});
