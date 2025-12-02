import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  Modal,
  TouchableOpacity,
  ScrollView,
} from 'react-native';

export type SortOrder = 'asc' | 'desc' | null;
export type PopulationFilter = 'all' | 'above15M';

interface FilterModalProps {
  visible: boolean;
  onClose: () => void;
  sortOrder: SortOrder;
  populationFilter: PopulationFilter;
  onSortChange: (order: SortOrder) => void;
  onPopulationFilterChange: (filter: PopulationFilter) => void;
}

const FilterModal: React.FC<FilterModalProps> = ({
  visible,
  onClose,
  sortOrder,
  populationFilter,
  onSortChange,
  onPopulationFilterChange,
}) => {
  return (
    <Modal
      visible={visible}
      transparent={true}
      animationType="slide"
      onRequestClose={onClose}
    >
      <View style={styles.overlay}>
        <View style={styles.modalContainer}>
          <View style={styles.header}>
            <Text style={styles.title}>Filtres et Tri</Text>
            <TouchableOpacity onPress={onClose} style={styles.closeButton}>
              <Text style={styles.closeText}>✕</Text>
            </TouchableOpacity>
          </View>

          <ScrollView style={styles.content}>
            {/* Section Tri */}
            <View style={styles.section}>
              <Text style={styles.sectionTitle}>📊 Ordre de tri</Text>
              
              <TouchableOpacity
                style={[
                  styles.option,
                  sortOrder === 'asc' && styles.optionSelected,
                ]}
                onPress={() => onSortChange('asc')}
              >
                <Text
                  style={[
                    styles.optionText,
                    sortOrder === 'asc' && styles.optionTextSelected,
                  ]}
                >
                  ↑ Ordre croissant (A → Z)
                </Text>
                {sortOrder === 'asc' && <Text style={styles.checkmark}>✓</Text>}
              </TouchableOpacity>

              <TouchableOpacity
                style={[
                  styles.option,
                  sortOrder === 'desc' && styles.optionSelected,
                ]}
                onPress={() => onSortChange('desc')}
              >
                <Text
                  style={[
                    styles.optionText,
                    sortOrder === 'desc' && styles.optionTextSelected,
                  ]}
                >
                  ↓ Ordre décroissant (Z → A)
                </Text>
                {sortOrder === 'desc' && <Text style={styles.checkmark}>✓</Text>}
              </TouchableOpacity>
            </View>

            {/* Section Population */}
            <View style={styles.section}>
              <Text style={styles.sectionTitle}>👥 Filtre par population</Text>
              
              <TouchableOpacity
                style={[
                  styles.option,
                  populationFilter === 'all' && styles.optionSelected,
                ]}
                onPress={() => onPopulationFilterChange('all')}
              >
                <Text
                  style={[
                    styles.optionText,
                    populationFilter === 'all' && styles.optionTextSelected,
                  ]}
                >
                  Tous les pays
                </Text>
                {populationFilter === 'all' && <Text style={styles.checkmark}>✓</Text>}
              </TouchableOpacity>

              <TouchableOpacity
                style={[
                  styles.option,
                  populationFilter === 'above15M' && styles.optionSelected,
                ]}
                onPress={() => onPopulationFilterChange('above15M')}
              >
                <Text
                  style={[
                    styles.optionText,
                    populationFilter === 'above15M' && styles.optionTextSelected,
                  ]}
                >
                  Population ≥ 15 millions
                </Text>
                {populationFilter === 'above15M' && (
                  <Text style={styles.checkmark}>✓</Text>
                )}
              </TouchableOpacity>
            </View>
          </ScrollView>

          <View style={styles.footer}>
            <TouchableOpacity
              style={styles.resetButton}
              onPress={() => {
                onSortChange('asc');
                onPopulationFilterChange('all');
              }}
            >
              <Text style={styles.resetButtonText}>Réinitialiser</Text>
            </TouchableOpacity>
            
            <TouchableOpacity style={styles.applyButton} onPress={onClose}>
              <Text style={styles.applyButtonText}>Appliquer</Text>
            </TouchableOpacity>
          </View>
        </View>
      </View>
    </Modal>
  );
};

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    justifyContent: 'flex-end',
  },
  modalContainer: {
    backgroundColor: '#fff',
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
    maxHeight: '80%',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 20,
    borderBottomWidth: 1,
    borderBottomColor: '#ecf0f1',
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#2c3e50',
  },
  closeButton: {
    padding: 5,
  },
  closeText: {
    fontSize: 24,
    color: '#7f8c8d',
  },
  content: {
    padding: 20,
  },
  section: {
    marginBottom: 25,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#2c3e50',
    marginBottom: 15,
  },
  option: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 15,
    backgroundColor: '#f8f9fa',
    borderRadius: 10,
    marginBottom: 10,
    borderWidth: 2,
    borderColor: 'transparent',
  },
  optionSelected: {
    backgroundColor: '#e3f2fd',
    borderColor: '#3498db',
  },
  optionText: {
    fontSize: 15,
    color: '#34495e',
  },
  optionTextSelected: {
    color: '#3498db',
    fontWeight: '600',
  },
  checkmark: {
    fontSize: 18,
    color: '#3498db',
    fontWeight: 'bold',
  },
  footer: {
    flexDirection: 'row',
    padding: 20,
    borderTopWidth: 1,
    borderTopColor: '#ecf0f1',
    gap: 10,
  },
  resetButton: {
    flex: 1,
    padding: 15,
    backgroundColor: '#ecf0f1',
    borderRadius: 10,
    alignItems: 'center',
  },
  resetButtonText: {
    color: '#7f8c8d',
    fontSize: 16,
    fontWeight: '600',
  },
  applyButton: {
    flex: 1,
    padding: 15,
    backgroundColor: '#3498db',
    borderRadius: 10,
    alignItems: 'center',
  },
  applyButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
});

export default FilterModal;
