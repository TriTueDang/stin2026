<template>
  <div class="dashboard-container">
    <div class="header-bar">
      <h1>{{ t[lang].title }}</h1>
      <div class="header-actions">
        <span class="user-greeting">👤 {{ loggedInUser }}</span>
        <button class="icon-btn settings-btn" @click="showSettings = true">
          ⚙️ {{ t[lang].settings }}
        </button>
        <button class="icon-btn logout-btn" @click="handleLogout">
          🚪 {{ t[lang].logout }}
        </button>
      </div>
    </div>

    <!-- Main action bar -->
    <section class="card action-bar">
      <div class="action-info">
        <span class="currency-label">{{ t[lang].baseCurrency }}:</span>
        <span class="badge">{{ currentBase }}</span>
      </div>
      <button class="main-btn" @click="fetchCurrentRates" :disabled="loading">
        {{ loading ? '...' : t[lang].refresh }}
      </button>
    </section>

    <!-- Settings Modal -->
    <div v-if="showSettings" class="modal-overlay" @click.self="saveSettings">
      <div class="modal modern-modal">
        <div class="modal-header">
          <h2>{{ t[lang].settings }}</h2>
          <button class="close-btn" @click="saveSettings">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label><strong>{{ t[lang].defaultBase }}:</strong></label>
            <select v-model="currentBase" class="modern-select">
              <option v-for="c in baseCurrencies" :key="c" :value="c">{{ c }}</option>
            </select>
          </div>

          <div class="form-group">
            <label><strong>{{ t[lang].preferredCurrencies }}:</strong></label>
            <div class="checkbox-grid">
              <label v-for="c in availableCurrencies" :key="c" class="check-item modern-check">
                <input type="checkbox" :value="c" v-model="watchedCurrencies" />
                <span class="check-text">{{ c }}</span>
              </label>
            </div>
          </div>

          <div class="form-group">
            <label><strong>{{ t[lang].language }}:</strong></label>
            <select v-model="lang" class="modern-select">
              <option value="cs">CZ</option>
              <option value="en">EN</option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button class="main-btn w-full" @click="saveSettings">
            {{ t[lang].saveSettings }}
          </button>
        </div>
      </div>
    </div>

    <!-- Error Modal -->
    <div v-if="error" class="modal-overlay">
      <div class="modal error-modal">
        <div class="modal-header error-header">
          <h2>⚠️ {{ t[lang].errorTitle }}</h2>
          <button class="close-btn" @click="closeError">×</button>
        </div>
        <div class="modal-body text-center">
          <p class="error-text">{{ t[lang].fetchError }}</p>
          <div v-if="backendErrors.length > 0" class="backend-errors-box">
             <ul class="error-list">
               <li v-for="msg in backendErrors" :key="msg">⚠️ {{ msg }}</li>
             </ul>
          </div>
          <p v-else class="error-subtext">{{ t[lang].tryAgain }}</p>

          <div class="last-data-box" v-if="lastValidDate">
            ({{ t[lang].usingLastData }}: {{ formatDate(lastValidDate) }})
          </div>
        </div>
        <div class="modal-footer">
          <button class="main-btn retry-btn w-full" @click="fetchCurrentRates">
            {{ t[lang].retry }}
          </button>
        </div>
      </div>
    </div>

    <!-- Current Rates -->
    <section v-if="currentData && currentData.exchangeRates" class="card">
      <div class="table-header">
        <h2>{{ t[lang].currentTable }}</h2>
        <span class="date-info">{{ formatDate(currentData.exchangeRates.timestamp) }}</span>
      </div>

      <!-- Statistics -->
      <div class="stats-row" v-if="currentData.strongestCurrency || currentData.weakestCurrency">
        <div class="stat-badge success" v-if="currentData.strongestCurrency">
          📈 {{ t[lang].strongest }}: <strong>{{ currentData.strongestCurrency }}</strong>
        </div>
        <div class="stat-badge danger" v-if="currentData.weakestCurrency">
          📉 {{ t[lang].weakest }}: <strong>{{ currentData.weakestCurrency }}</strong>
        </div>
      </div>

      <!-- Warning Banner -->
      <div v-if="currentData.warning" class="warning-box mb-4">
        ⚠️ {{ currentData.warning }}
      </div>

      <div class="table-responsive">
        <table class="rate-table modern-table">
          <thead>
            <tr>
              <th>{{ t[lang].currency }}</th>
              <th>{{ t[lang].rate }} ({{ currentBase }})</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="symbol in filteredQuotes" :key="symbol">
              <td>
                <div class="currency-cell">
                  <strong>{{ symbol }}</strong>
                </div>
              </td>
              <td>
                <span class="rate-value">{{ currentData.exchangeRates.quotes[currentBase + symbol]?.toFixed(4) || 'N/A' }}</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <!-- History Controls -->
    <section class="card history-card">
      <h2>{{ t[lang].history }}</h2>
      <div class="history-controls">
        <div class="date-group">
          <input type="date" v-model="startDate" class="modern-input" />
          <span class="date-separator">-</span>
          <input type="date" v-model="endDate" class="modern-input" />
        </div>
        <button class="secondary-btn" @click="fetchTimeframe(false)" :disabled="loading || historyLoading">
          {{ (loading || historyLoading) ? '...' : t[lang].loadHistory }}
        </button>
      </div>

      <!-- History Warning Banner -->
      <div v-if="historyWarning" class="warning-box mb-4">
        <div>
          <strong>⚠️ {{ historyWarning }}</strong>
          <div v-if="historyData && historyData.start_date" class="mt-1 text-sm opacity-80">
            {{ t[lang].period }}: {{ historyData.start_date }} – {{ historyData.end_date }}
          </div>
        </div>
      </div>

      <!-- Average Table from History -->
      <div v-if="historyStatsData && historyStatsData.average" class="average-table-section mt-4">
        <h3>{{ t[lang].averageSelected }}</h3>
        <div class="table-responsive">
          <table class="rate-table modern-table">
            <thead>
              <tr>
                <th>{{ t[lang].currency }}</th>
                <th>{{ t[lang].averageRate }} ({{ currentBase }})</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="symbol in filteredQuotes" :key="symbol">
                <td><strong>{{ symbol }}</strong></td>
                <td>
                  <span class="rate-value">
                    {{ historyStatsData.average[symbol] ? historyStatsData.average[symbol].toFixed(4) : 'N/A' }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Historical Charts per Currency -->
      <div v-if="historyData && historyData.quotes && chartPoints.length > 0" class="history-charts-wrapper mt-4">
        <div v-for="(chartLine, index) in chartLines" :key="chartLine.currency" class="individual-chart-card mb-4">
          <h4 class="chart-title">{{ chartLine.currency }} vs {{ currentBase }}</h4>
          <svg class="svg-chart" viewBox="0 0 800 200" preserveAspectRatio="none">
            <!-- Grid Lines -->
            <line v-for="i in 3" :key="'grid'+i" x1="45" :y1="getChartGridY(i)" x2="800" :y2="getChartGridY(i)" class="grid-line" />

            <!-- Y Axis Labels -->
            <text v-for="i in 3" :key="'ytxt'+i" x="40" :y="getChartGridY(i) + 4" class="axis-text y-axis">
              {{ getChartGridVal(chartLine.min, chartLine.max, i).toFixed(4) }}
            </text>

            <!-- Line -->
            <path :d="chartLine.path" class="chart-line" :stroke="colors[index % colors.length]" fill="none" stroke-width="3" stroke-linejoin="round" />

            <!-- Points -->
            <circle v-for="(pt, pi) in chartLine.points" :key="pi" :cx="pt.x" :cy="pt.y" r="4" :fill="colors[index % colors.length]" class="chart-point">
              <title>{{ pt.date }}: {{ pt.val.toFixed(4) }}</title>
            </circle>

            <!-- X Axis Labels -->
             <g v-if="chartLine.points.length > 0">
               <text :x="chartLine.points[0].x" y="195" class="axis-text x-axis start">{{ chartPoints[0].date }}</text>
               <text v-if="chartLine.points.length > 2" :x="chartLine.points[Math.floor(chartLine.points.length/2)].x" y="195" class="axis-text x-axis middle">{{ chartPoints[Math.floor(chartPoints.length/2)].date }}</text>
               <text :x="chartLine.points[chartLine.points.length - 1].x" y="195" class="axis-text x-axis end">{{ chartPoints[chartPoints.length - 1].date }}</text>
             </g>
          </svg>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import apiClient from '@/services/api';

const router = useRouter();
const loggedInUser = ref(localStorage.getItem('user') || 'Admin');

const handleLogout = () => {
  localStorage.removeItem('isAuthenticated');
  localStorage.removeItem('user');
  localStorage.removeItem('basicAuthToken');
  router.push('/login');
};

const lang = ref('cs');
const loading = ref(false);
const error = ref(null);
const showSettings = ref(false);
const historyLoading = ref(false);

// Configuration Defaults
const availableCurrencies = ['USD', 'EUR', 'CZK', 'GBP', 'CHF', 'JPY', 'PLN', 'HUF', 'AUD', 'CAD', 'CNY', 'SEK', 'NOK', 'DKK'];
const baseCurrencies = ['USD', 'EUR', 'CZK'];
const watchedCurrencies = ref(['EUR', 'CZK', 'USD', 'GBP']);
const currentBase = ref('EUR');

const currentData = ref(null);
const historyData = ref(null);
const historyStatsData = ref(null);
const historyWarning = ref(null);
const getFormattedDate = (date) => date.toISOString().split('T')[0];
const today = new Date();
const yesterday = new Date(today);
yesterday.setDate(today.getDate() - 1);
const oneMonthAgo = new Date(yesterday);
oneMonthAgo.setMonth(yesterday.getMonth() - 1);

const startDate = ref(getFormattedDate(oneMonthAgo));
const endDate = ref(getFormattedDate(yesterday));
const lastValidDate = ref(null);
const backendErrors = ref([]);

const colors = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899', '#06b6d4'];

// Translations
const t = {
  cs: {
    title: 'Měnový analyzátor',
    baseCurrency: 'Základní měna',
    settings: 'Nastavení',
    defaultBase: 'Výchozí základní měna',
    preferredCurrencies: 'Preferované měny',
    language: 'Jazyk',
    saveSettings: 'Uložit nastavení a aktualizovat',
    errorTitle: 'Chyba',
    fetchError: 'Nepodařilo se načíst data z ExchangeRate API',
    tryAgain: 'Prosím zkuste to znovu později',
    usingLastData: 'Použita poslední dostupná data z',
    retry: 'Zkusit znovu',
    refresh: 'Aktualizovat kurzy',
    currentTable: 'Aktuální kurzy',
    currency: 'Měna',
    rate: 'Kurz',
    history: 'Historie',
    loadHistory: 'Načíst historii',
    strongest: 'Nejsilnější',
    weakest: 'Nejslabší',
    averageSelected: 'Průměr vybraných měn',
    averageRate: 'Průměrný kurz',
    logout: 'Odhlásit se',
    period: 'Období'
  },
  en: {
    title: 'Exchange Rates Analyzer',
    baseCurrency: 'Base Currency',
    settings: 'Settings',
    defaultBase: 'Default base currency',
    preferredCurrencies: 'Preferred currencies',
    language: 'Language',
    saveSettings: 'Save settings & refresh',
    errorTitle: 'Error',
    fetchError: 'Unable to fetch data from ExchangeRate API',
    tryAgain: 'Please try again later',
    usingLastData: 'Using last available data from',
    retry: 'Retry',
    refresh: 'Refresh Rates',
    currentTable: 'Current Rates',
    currency: 'Currency',
    rate: 'Rate',
    history: 'History',
    loadHistory: 'Load History',
    strongest: 'Strongest',
    weakest: 'Weakest',
    averageSelected: 'Average of selected currencies',
    averageRate: 'Average Rate',
    logout: 'Logout',
    period: 'Period'
  }
};

const filteredQuotes = computed(() => {
  return watchedCurrencies.value.filter(c => c !== currentBase.value);
});

const formatDate = (ts) => {
  if (!ts) return '';
  return new Date(ts * 1000).toLocaleString(lang.value === 'cs' ? 'cs-CZ' : 'en-US');
};

const saveSettings = async () => {
  backendErrors.value = [];
  try {
    await apiClient.post('/api/rates/settings', {
      baseCurrency: currentBase.value,
      watchedCurrencies: watchedCurrencies.value,
      lang: lang.value
    });
    showSettings.value = false; // Close settings only on success
    await fetchCurrentRates();
    // Fetch timeframe with delay to avoid burst limits
    fetchTimeframe(true);
  } catch(e) {
    handleApiError(e);
  }
};


const closeError = () => {
  error.value = null;
  backendErrors.value = [];
};

const handleApiError = (err) => {
  console.error(err);
  error.value = "API Error";
  backendErrors.value = [];

  if (err.response && err.response.data) {
    if (err.response.data.errors && Array.isArray(err.response.data.errors)) {
      backendErrors.value = err.response.data.errors;
    } else if (err.response.data.message) {
      backendErrors.value = [err.response.data.message];
    } else {
      backendErrors.value = [JSON.stringify(err.response.data)];
    }
  } else if (err.message) {
    backendErrors.value = [err.message];
  }
};

const fetchCurrentRates = async () => {
  loading.value = true;
  error.value = null;
  backendErrors.value = [];
  if (currentData.value) currentData.value.warning = null;


  try {
    const response = await apiClient.post('/api/rates/current', {
      base: currentBase.value,
      watched: watchedCurrencies.value
    });
    currentData.value = response.data;
    if (response.data && response.data.exchangeRates) {
      lastValidDate.value = response.data.exchangeRates.timestamp;
    }
  } catch (err) {
    handleApiError(err);
  } finally {
    loading.value = false;
  }
};

const fetchTimeframe = async (withDelay = false) => {
  if (withDelay) {
    historyLoading.value = true;
    await new Promise(r => setTimeout(r, 2000));
  }
  loading.value = true;
  historyLoading.value = true;
  historyData.value = null;
  historyStatsData.value = null;
  historyWarning.value = null;
  backendErrors.value = [];

  try {
    const response = await apiClient.post('/api/rates/history', {
      base: currentBase.value,
      startDate: startDate.value,
      endDate: endDate.value,
      watched: watchedCurrencies.value
    });
    historyData.value = response.data.history;
    historyStatsData.value = response.data.statistics;
    historyWarning.value = response.data.warning;
  } catch (err) {
    handleApiError(err);
  } finally {
    loading.value = false;
    historyLoading.value = false;
  }
};

// -- CHART LOGIC -- //
const chartPoints = computed(() => {
  if (!historyData.value || !historyData.value.quotes) return [];
  return Object.keys(historyData.value.quotes)
    .sort()
    .map(date => ({
      date,
      rates: historyData.value.quotes[date]
    }));
});

const getChartGridY = (i) => {
  return 20 + ((i - 1) * 70);
};

const getChartGridVal = (min, max, i) => {
  const ratio = (3 - i) / 2;
  return min + (max - min) * ratio;
};

const chartLines = computed(() => {
  const pts = chartPoints.value;
  if(pts.length === 0) return [];

  const width = 750;
  const height = 140;

  return filteredQuotes.value.map(sym => {
    let min = Infinity;
    let max = -Infinity;

    // Calculate local min max
    pts.forEach(p => {
      const val = p.rates[currentBase.value + sym];
      if (val !== undefined && val !== null) {
        if(val < min) min = val;
        if(val > max) max = val;
      }
    });

    if (min === Infinity) { min = 0; max = 1; }
    const padding = (max - min) * 0.1 || max * 0.1;
    min = Math.max(0, min - padding);
    max = max + padding;

    const range = max - min || 1;
    const points = [];

    pts.forEach((p, index) => {
      const val = p.rates[currentBase.value + sym];
      if(val !== undefined && val !== null) {
        const x = 50 + (pts.length > 1 ? (index / (pts.length - 1)) * width : width / 2);
        const y = 160 - ((val - min) / range) * height;
        points.push({ x, y, val, date: p.date });
      }
    });

    let path = "";
    points.forEach((pt, i) => {
      path += (i === 0 ? `M ${pt.x} ${pt.y}` : ` L ${pt.x} ${pt.y}`);
    });

    return { currency: sym, points, path, min, max };
  }).filter(l => l.points.length > 0);
});

// Load Settings from File Initialy
onMounted(async () => {
  try {
    const res = await apiClient.get('/api/rates/settings');
    if (res.data) {
      if (res.data.baseCurrency) currentBase.value = res.data.baseCurrency;
      if (res.data.watchedCurrencies && res.data.watchedCurrencies.length > 0) watchedCurrencies.value = res.data.watchedCurrencies;
      if (res.data.lang) lang.value = res.data.lang;
    }
  } catch(e) {
    console.error("No custom settings loaded, using defaults.");
  }

  // Followed by fetching standard data
  await fetchCurrentRates();
  // Fetch history with delay
  fetchTimeframe(true);
});
</script>

<style scoped>
/* CSS Variables for Permanently Dark Theme */
.dashboard-container {
  --bg-body: #111827;
  --bg-card: #1f2937;
  --bg-input: #374151;
  --bg-hover: #4b5563;
  --text-main: #f3f4f6;
  --text-muted: #9ca3af;
  --border-color: #374151;
  --border-strong: #4b5563;
  --primary: #3b82f6;
  --primary-hover: #2563eb;
  --pill-bg: #374151;
  --card-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.2), 0 2px 4px -1px rgba(0, 0, 0, 0.1);

  min-height: 100vh;
  background-color: var(--bg-body);
  color: var(--text-main);
  font-family: 'Inter', 'Segoe UI', system-ui, sans-serif;
  padding: 30px 20px;
}

/* Base Styles */
h1, h2, h3, h4, p { margin-top: 0; color: var(--text-main); }
h3 { font-size: 1.1rem; margin-bottom: 12px; }
h4 { margin-top: 0; margin-bottom: 16px; font-weight: 600; font-size: 1.05rem; color: var(--text-main); }
.warning-box {
  background: rgba(245, 158, 11, 0.1);
  border: 1px solid #f59e0b;
  color: #f59e0b;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 10px;
}
.mt-4 { margin-top: 24px; }
.mb-4 { margin-bottom: 24px; }

/* Header */
.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
}
.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
.user-greeting {
  font-weight: 500;
  color: var(--text-muted);
  margin-right: 8px;
}
.header-bar h1 {
  margin: 0;
  font-size: 1.8rem;
  font-weight: 700;
  letter-spacing: -0.025em;
}

/* Cards */
.card {
  max-width: 800px;
  margin: 0 auto 24px auto;
  background: var(--bg-card);
  padding: 24px;
  border-radius: 16px;
  box-shadow: var(--card-shadow);
  border: 1px solid var(--border-color);
  transition: all 0.3s;
}

/* Action Bar */
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
}
.action-info {
  display: flex;
  align-items: center;
  gap: 12px;
}
.currency-label {
  font-weight: 500;
  color: var(--text-muted);
}
.badge {
  background: rgba(37, 99, 235, 0.1);
  color: var(--primary);
  padding: 6px 14px;
  border-radius: 20px;
  font-weight: 700;
  font-size: 0.95rem;
  border: 1px solid rgba(37, 99, 235, 0.2);
}

/* Buttons */
button {
  font-family: inherit;
  cursor: pointer;
  transition: all 0.2s ease;
}
.icon-btn {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  padding: 8px 16px;
  border-radius: 8px;
  font-weight: 600;
  color: var(--text-main);
  display: flex;
  align-items: center;
  gap: 8px;
}
.icon-btn:hover { background: var(--bg-hover); }

.main-btn {
  background: var(--primary);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 600;
}
.main-btn:hover:not(:disabled) { background: var(--primary-hover); transform: translateY(-1px); }
.main-btn:disabled { opacity: 0.6; cursor: not-allowed; transform: none; }

.secondary-btn {
  background: var(--bg-card);
  color: var(--text-main);
  border: 1px solid var(--border-strong);
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 600;
}
.secondary-btn:hover:not(:disabled) { background: var(--bg-hover); }

/* Settings Form */
.form-group {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.form-group label { color: var(--text-muted); font-size: 0.9rem; }
.modern-select, .modern-input {
  width: 100%;
  padding: 10px 14px;
  border-radius: 8px;
  border: 1px solid var(--border-strong);
  background-color: var(--bg-input);
  font-size: 1rem;
  color: var(--text-main);
  outline: none;
  color-scheme: dark;
}
.modern-select:focus, .modern-input:focus {
  border-color: var(--primary);
  background-color: var(--bg-card);
}

.checkbox-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(85px, 1fr));
  gap: 12px;
  background: var(--bg-input);
  padding: 16px;
  border: 1px solid var(--border-color);
  border-radius: 12px;
}
.modern-check {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
.modern-check input { width: 16px; height: 16px; accent-color: var(--primary); }
.check-text { font-size: 0.95rem; font-weight: 500; color: var(--text-main); }

/* Modals */
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal {
  background: var(--bg-card);
  width: 90%;
  max-width: 480px;
  border-radius: 16px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.2);
  border: 1px solid var(--border-color);
  overflow: hidden;
}
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid var(--border-color);
}
.modal-header h2 {
  margin: 0;
  font-size: 1.4rem;
  line-height: 1;
}
.close-btn {
  background: var(--bg-input);
  border: none;
  font-size: 1.8rem;
  color: var(--text-muted);
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s;
  cursor: pointer;
  padding-bottom: 5px; /* Fine-tuned vertical adjustment for the × character baseline */
}
.close-btn:hover {
  background: var(--bg-hover);
  color: #ef4444;
}
.modal-body { padding: 24px; }
.modal-footer {
  padding: 20px 24px;
  background: var(--bg-input);
  border-top: 1px solid var(--border-color);
}
.w-full { width: 100%; }

/* Error Modal Specifics */
.error-modal {
  border: 1px solid #ef4444;
  box-shadow: 0 10px 25px -5px rgba(239, 68, 68, 0.15);
}
.error-header {
  border-bottom: 1px solid rgba(239, 68, 68, 0.2);
  background: rgba(239, 68, 68, 0.05);
}
.error-header h2 {
  color: #ef4444;
  display: flex;
  align-items: center;
  gap: 10px;
}
.text-center { text-align: center; }
.error-text { font-size: 1.15rem; font-weight: 600; margin-bottom: 8px; color: var(--text-main); }
.error-subtext { color: var(--text-muted); margin-bottom: 24px; }
.last-data-box {
  background: var(--bg-input);
  border: 1px solid var(--border-color);
  padding: 12px;
  border-radius: 8px;
  font-size: 0.95rem;
  color: var(--text-muted);
  margin-bottom: 8px;
}
.retry-btn { background: #ef4444; }
.retry-btn:hover:not(:disabled) { background: #dc2626; }

.backend-errors-box {
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.2);
  border-radius: 8px;
  padding: 12px;
  margin: 15px 0;
  text-align: left;
}
.error-list {
  margin: 0;
  padding-left: 0;
  list-style: none;
}
.error-list li {
  color: #ef4444;
  font-size: 0.9rem;
  margin-bottom: 4px;
}
.error-list li:last-child { margin-bottom: 0; }

/* Tables */
.table-header { display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 20px; }
.date-info { font-size: 0.9rem; color: var(--text-muted); font-weight: 500; }
.table-responsive { overflow-x: auto; }
.rate-table { width: 100%; border-collapse: separate; border-spacing: 0; }
.rate-table th {
  background: var(--bg-input);
  text-align: left;
  padding: 14px 16px;
  font-size: 0.85rem;
  text-transform: uppercase;
  color: var(--text-muted);
  border-bottom: 1px solid var(--border-color);
}
.rate-table th:first-child { border-top-left-radius: 8px; }
.rate-table th:last-child { border-top-right-radius: 8px; }
.rate-table td {
  padding: 16px;
  border-bottom: 1px solid var(--border-color);
  color: var(--text-main);
}
.rate-table tr:hover td { background: var(--bg-hover); }
.currency-cell { display: flex; align-items: center; gap: 12px; }
.rate-value { font-family: 'Roboto Mono', monospace; font-weight: 600; color: var(--text-main); }

/* Stats */
.stats-row { display: flex; gap: 12px; margin-bottom: 20px; }
.stat-badge {
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 500;
}
.stat-badge.success { background: rgba(5, 150, 105, 0.1); color: #10b981; border: 1px solid #10b981; }
.stat-badge.danger { background: rgba(220, 38, 38, 0.1); color: #ef4444; border: 1px solid #ef4444; }

/* History Section */
.history-controls {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
  margin: 20px 0;
}
.date-group { display: flex; align-items: center; gap: 8px; }
.date-separator { color: var(--text-muted); font-weight: bold; }

/* Individual Multi-Charts Section */
.history-charts-wrapper {
  display: flex;
  flex-direction: column;
  gap: 24px;
  width: 100%;
}
.individual-chart-card {
  background: var(--bg-input);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 16px 20px;
}
.chart-title {
  color: var(--text-main);
  font-size: 1.15rem;
  font-weight: 600;
  margin-top: 0;
  margin-bottom: 16px;
}
.svg-chart {
  width: 100%;
  height: auto;
  border-bottom: 2px solid var(--border-color);
  border-left: 2px solid var(--border-color);
}
.grid-line { stroke: var(--border-color); stroke-width: 1; stroke-dasharray: 4; }
.axis-text { font-family: sans-serif; font-size: 11px; fill: var(--text-muted); }
.y-axis { text-anchor: end; }
.x-axis.start { text-anchor: start; }
.x-axis.middle { text-anchor: middle; }
.x-axis.end { text-anchor: end; }
.chart-point { transition: r 0.2s; cursor: crosshair; }
.chart-point:hover { r: 6; }
</style>

