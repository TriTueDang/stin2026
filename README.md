# 🌍 Měnový Analyzátor (Exchange Rates Analyzer)

[![Build and Deploy](https://github.com/TriTueDang/stin2026/actions/workflows/deploy.yml/badge.svg)](https://github.com/TriTueDang/stin2026/actions/workflows/deploy.yml)
[![Azure Deployed](https://img.shields.io/badge/Deployed%20to-Azure-0078D4?logo=microsoft-azure&logoColor=white)](https://azure.microsoft.com/cs-cz)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.5-4FC08D?logo=vuedotjs&logoColor=white)](https://vuejs.org/)

Moderní webová aplikace pro sledování a analýzu směnných kurzů v reálném čase i historii. Aplikace je plně nasazena v cloudu **Microsoft Azure**.

Link na aplikaci: https://currency-app-bvh4duf3cvbqbmew.germanywestcentral-01.azurewebsites.net/

---

## ✨ Klíčové Vlastnosti

- 📈 **Aktuální Kurzy**: Sledování kurzů vůči EUR, USD nebo CZK v reálném čase.
- 📅 **Historické Přehledy**: Detailní pohled na vývoj měn ve zvoleném časovém období.
- 📊 **SVG Analýza**: Vlastní dynamické grafy pro vizualizaci trendů bez externích závislostí.
- 🔢 **Statistické Výpočty**: Automatický výpočet průměrných hodnot, nejsilnější a nejslabší měny.
- 🛡️ **Fallback**: Systém ukládání dat, který zobrazí poslední známé hodnoty i při výpadku API nebo překročení limitů (Error 429).
- ⚙️ **Personalizace**: Uživatelské nastavení (jazyk, preferované měny) uložené na serveru.
- 🌑 **Moderní Dark UI**: Tmavý vzhled optimalizovaný pro desktop i mobilní zařízení.

---

## 🛠️ Technologie & Implementace

### Backend (Spring Boot)
- **Java 17 & Spring Boot 4**: Jádro aplikace.
- **Spring WebClient**: Reaktivní komunikace s externím poskytovatelem kurzů.
- **File-based Caching**: Vlastní implementace `FileStorageService`, která uchovává JSON snapshoty dat v adresáři `data/`.
- **Security**: Zabezpečený přístup k API endpointům.
- **Lombok & SLF4J**: Pro zjednodušení kódu a logování.

### Frontend (Vue.js 3)
- **Vite & Vue 3 (Composition API)**: Rozhrani pro uživatele.
- **Axios**: Správa API dotazů.
- **SVG Charts**: Vizualizace dat.
- **Responsivita**: Flexibilní layout využívající CSS proměnné a moderní grid/flexbox.

### CI/CD & Cloud
- **GitHub Actions**: Automatizovaný build frontend (Vite), integrace do backendu a deploy do Azure.
- **Azure App Service**: Hostování Spring Boot JAR aplikace v cloudu.

---

## 📂 Struktura Projektu

```bash
stin2026/
├── .github/workflows/    # CI/CD konfigurace pro Azure
├── backend/              # Java Spring Boot aplikace
│   ├── src/main/java/    # Logika, Služby (ExchangeRateService)
│   ├── data/             # Lokální cache (JSON snapshoty)
│   └── pom.xml           # Backend závislosti
└── frontend/             # Vue.js 3 aplikace
    ├── src/components/   # UI Komponenty (ExchangeRate.vue)
    ├── src/services/     # API klient (Axios config)
    └── package.json      # Frontend závislosti
```

---

## 🧪 Testování & Kvalita

Projekt je testovaný a snaží se dodržovat zásady čistého kódu.
- **Backend**: Unit testy s JUnit 5 a Mockito pro izolované testování business logiky.
- **Code Coverage**: Integrovaný **JaCoCo** report pro sledování pokrytí kódu na backendu.

---

## 🚀 Instalace a Spuštění

### Lokální vývoj (Backend)
1. Přejděte do složky `backend`.
2. Ujistěte se, že máte nainstalovanou Javu 17+.
3. Nastavte proměnné prostředí pro API (v `.env`):
   - `EXCHANGE_API_URL`: URL adresa API, např. https://exchangerate.host/
   - `EXCHANGE_API_KEY`: Váš privátní API klíč, např. 1234567890
4. Spusťte pomocí Maven wrapperu:
   ```powershell
   .\mvnw spring-boot:run
   ```

### Lokální vývoj (Frontend)
1. Přejděte do složky `frontend`.
2. Nainstalujte závislosti: `npm install`.
3. Spusťte vývojový server:
   ```bash
   npm run dev
   ```

---

## ☁️ Deployment (Azure)

Aplikace je automaticky nasazována na **Azure Web App** při každém pushi do větve `main`.
1. Workflow sestaví frontend pomocí `npm run build`.
2. Výsledné soubory zkopíruje do `backend/src/main/resources/static`.
3. Sestaví monolitický JAR soubor.
4. Nahraje artifact do Azure pomocí `azure/webapps-deploy@v3`.

---
