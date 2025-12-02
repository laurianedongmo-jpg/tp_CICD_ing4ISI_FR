import React from 'react';
import { ActivityIndicator, StyleSheet, Text, View } from 'react-native';

interface ActivityLoadingProps {
  message?: string;
  size?: 'small' | 'large';
}

/**
 * Composant de chargement centralisé avec indicateur d'activité
 */
export default function ActivityLoading({ 
  message = 'Chargement...', 
  size = 'large' 
}: ActivityLoadingProps) {
  return (
    <View style={styles.container}>
      <ActivityIndicator size={size} color="#007AFF" />
      {message && <Text style={styles.message}>{message}</Text>}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#f5f5f5',
  },
  message: {
    marginTop: 16,
    fontSize: 16,
    color: '#666',
  },
});
