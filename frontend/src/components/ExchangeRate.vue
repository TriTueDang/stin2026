<template>
  <div class="container">
    <div class="lang-switch">
      <button @click="lang = 'cs'" :class="{ active: lang === 'cs' }">CZ</button>
      <button @click="lang = 'en'" :class="{ active: lang === 'en' }">EN</button>
    </div>

    <h1>{{ t[lang].title }}</h1>

    <section class="card config-section">
      <div class="form-group">
        <label><strong>{{ t[lang].baseCurrency }}:</strong></label>
        <select v-model="currentBase" @change="fetchCurrentRates">
          <option v-for="c in availableCurrencies" :key="c" :value="c">{{ c }}</option>
        </select>
      </div>

      <div class="watchlist">
        <p><strong>{{ t[lang].watchlist }}:</strong></p>
        <div class="checkbox-grid">
          <label v-for="c in availableCurrencies" :key="c" class="check-item">
            <input type="checkbox" :value="c" v-model="watchedCurrencies" /> {{ c }}
          </label>
        </div>
      </div>

      <button class="main-btn" @click="fetchCurrentRates" :disabled="loading">
        {{ loading ? '...' : t[lang].refresh }}
      </button>
    </section>

    <section v-if="currentData" class="card">
      <div class="table-header">
        <h2>{{ t[lang].currentTable }}</h2>
        <span class="date-info">{{ formatDate(currentData.timestamp) }}</span>
      </div>

      <table class="rate-table">
        <thead>
          <tr>
            <th>{{ t[lang].currency }}</th>
            <th>{{ t[lang].rate }} ({{ currentBase }})</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="symbol in filteredQuotes" :key="symbol">
            <td><strong>{{ symbol }}</strong></td>
            <td>{{ currentData.quotes[currentBase + symbol]?.toFixed(4) || 'N/A' }}</td>
          </tr>
        </tbody>
      </table>
    </section>

    <section class="card">
      <h2>{{ t[lang].history }}</h2>
      <div class="form-row">
        <input type="date" v-model="startDate" />
        <input type="date" v-model="endDate" />
        <button @click="fetchTimeframe" :disabled="loading">{{ t[lang].loadHistory }}</button>
      </div>

      <div v-if="historyData" class="history-results">
        <div v-for="(rates, date) in historyData.quotes" :key="date" class="history-day">
          <span class="day-date">{{ date }}</span>:
          <span class="day-val">CZK: {{ rates[currentBase + 'CZK']?.toFixed(2) || 'N/A' }}</span>
        </div>
      </div>
    </section>

    <div v-if="error" class="error-msg">{{ error }}</div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import apiClient from '@/services/api';

const lang = ref('cs');
const loading = ref(false);
const error = ref(null);

// Konfigurace měn
const availableCurrencies = ['USD', 'EUR', 'CZK', 'GBP', 'CHF', 'JPY', 'PLN', 'HUF', 'AUD', 'CAD', 'CNY', 'SEK', 'NOK', 'DKK'];
const watchedCurrencies = ref(['EUR', 'CZK', 'USD', 'GBP']); // Výchozí sledované
const currentBase = ref('EUR');

const currentData = ref(null);
const historyData = ref(null);
const startDate = ref('2026-03-01');
const endDate = ref('2026-03-19');

// Překlady
const t = {
  cs: {
    title: 'Kurzovní lístek',
    baseCurrency: 'Základní měna',
    watchlist: 'Sledované měny',
    refresh: 'Aktualizovat kurzy',
    currentTable: 'Aktuální kurzy',
    currency: 'Měna',
    rate: 'Kurz',
    history: 'Historie',
    loadHistory: 'Načíst historii',
  },
  en: {
    title: 'Exchange Rate List',
    baseCurrency: 'Base Currency',
    watchlist: 'Watchlist',
    refresh: 'Refresh Rates',
    currentTable: 'Current Rates',
    currency: 'Currency',
    rate: 'Rate',
    history: 'History',
    loadHistory: 'Load History',
  }
};

// Filtrované kurzy pro zobrazení (jen ty, co uživatel zaškrtl a nejsou base)
const filteredQuotes = computed(() => {
  return watchedCurrencies.value.filter(c => c !== currentBase.value);
});

const formatDate = (ts) => {
  if (!ts) return '';
  return new Date(ts * 1000).toLocaleString(lang.value === 'cs' ? 'cs-CZ' : 'en-US');
};

const fetchCurrentRates = async () => {
  loading.value = true;
  error.value = null;
  try {
    const response = await apiClient.get(`/api/rates/${currentBase.value}`);
    currentData.value = response.data;
  } catch (err) {
    error.value = "API Error";
  } finally {
    loading.value = false;
  }
  console.log(currentData.value);
  console.log(filteredQuotes.value);
};

const fetchTimeframe = async () => {
  loading.value = true;
  try {
    const response = await apiClient.get(`/api/rates/timeframe/${currentBase.value}/${startDate.value}/${endDate.value}`);
    historyData.value = response.data;
  } catch (err) {
    error.value = "History API Error";
  } finally {
    loading.value = false;
  }
};

// Načíst data při startu
fetchCurrentRates();
</script>

<style scoped>
.container { max-width: 900px; margin: 0 auto; font-family: 'Segoe UI', Tahoma, sans-serif; padding: 20px; color: #333; }
.lang-switch { display: flex; justify-content: flex-end; gap: 5px; margin-bottom: 10px; }
.lang-switch button { border: 1px solid #ccc; background: white; cursor: pointer; padding: 5px 10px; border-radius: 4px; }
.lang-switch button.active { background: #42b983; color: white; border-color: #42b983; }

.card { background: white; padding: 25px; border-radius: 12px; margin-bottom: 25px; border: 1px solid #eee; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
.config-section { display: flex; flex-direction: column; gap: 15px; }

.checkbox-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(80px, 1fr)); gap: 10px; background: #fdfdfd; padding: 10px; border: 1px solid #f0f0f0; border-radius: 6px; }
.check-item { font-size: 14px; cursor: pointer; display: flex; align-items: center; gap: 5px; }

select { padding: 8px; border-radius: 4px; border: 1px solid #ddd; width: 150px; }
.main-btn { background: #2c3e50; color: white; border: none; padding: 12px; border-radius: 6px; cursor: pointer; font-weight: bold; }
.main-btn:hover { background: #34495e; }

.table-header { display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 15px; }
.date-info { font-size: 0.9em; color: #666; }

.rate-table { width: 100%; border-collapse: collapse; }
.rate-table th { background: #f8f9fa; text-align: left; padding: 12px; border-bottom: 2px solid #eee; }
.rate-table td { padding: 12px; border-bottom: 1px solid #eee; }
.rate-table tr:hover { background: #fcfcfc; }

.history-results { display: flex; flex-direction: column; gap: 8px; margin-top: 15px; }
.history-day { background: #f8f9fa; padding: 8px 15px; border-radius: 4px; font-family: monospace; }
.day-date { font-weight: bold; color: #42b983; }
</style>