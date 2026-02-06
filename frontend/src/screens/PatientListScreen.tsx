import React, {useEffect, useState} from 'react';
import {View, Text, FlatList, StyleSheet, ActivityIndicator, SafeAreaView} from 'react-native';
import api from '../api';

interface Patient {
  id: number;
  firstname: string;
  lastname: string;
  birthdate?: string;
}

export default function PatientListScreen() {
  const [patients, setPatients] = useState<Patient[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchPatients();
  }, []);

  async function fetchPatients() {
    setLoading(true);
    setError(null);
    try {
      // Versuche zuerst /patients, fallback auf /api/patients
      let resp = await api.get<Patient[]>('/patients').catch(async (e) => {
        // falls 404 oder Fehler, fallback
        try {
          return await api.get<Patient[]>('/api/patients');
        } catch (e2) {
          throw e2;
        }
      });

      // axios response kann Promise<Response> oder Response
      const data = (resp as any).data || (await resp).data;
      setPatients(data || []);
    } catch (e: any) {
      setError(e.message || 'Fehler beim Laden der Patienten');
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" />
      </View>
    );
  }

  if (error) {
    return (
      <SafeAreaView style={styles.container}>
        <Text style={styles.error}>Fehler: {error}</Text>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.title}>Patienten</Text>
      <FlatList
        data={patients}
        keyExtractor={(item) => item.id.toString()}
        renderItem={({item}) => (
          <View style={styles.item}>
            <Text style={styles.name}>{item.firstname} {item.lastname}</Text>
            <Text style={styles.meta}>{item.birthdate || 'kein Geburtsdatum'}</Text>
          </View>
        )}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {flex: 1, padding: 16},
  center: {flex: 1, justifyContent: 'center', alignItems: 'center'},
  title: {fontSize: 22, fontWeight: '600', marginBottom: 12},
  item: {padding: 12, borderBottomWidth: 1, borderColor: '#ddd'},
  name: {fontSize: 18},
  meta: {color: '#666'} ,
  error: {color: 'red'}
});
