# 🚜 Filiera Agricola

Benvenuto nella **Piattaforma di Filiera Agricola**! 🌱
Questo progetto è un sistema backend basato su **Spring Boot** per la gestione, tracciabilità e vendita di prodotti agroalimentari, garantendo trasparenza dal produttore al consumatore.

---

## 📚 Documentazione

La documentazione tecnica Javadoc è disponibile online qui:
👉 **[Javadoc & API Reference](https://filippoian.github.io/FilieraAgricola-IDS2425/)**

---

## ✨ Funzionalità Principali

*   **🔗 Tracciabilità Completa**: Ogni prodotto ha una storia. Gestione di lotti, fasi di trasformazione e trasporto tramite un grafo di tracciabilità.
*   **🛒 Marketplace & Bundle**: Vendita di prodotti singoli o pacchetti (bundle) promozionali creati dai distributori.
*   **🗺️ Mappa Interattiva**: Visualizzazione GeoJSON dei punti della filiera (Aziende, Trasformatori, Distributori). [Visualizza file mappa nel codice](src/main/resources/static/mappa.html) (Disponibile all'indirizzo `http://localhost:8080/mappa.html` a server avviato).
*   **👥 Gestione Ruoli Avanzata**: Sistema multi-ruolo (Produttore, Trasformatore, Distributore, Curatore, Gestore, Acquirente) con permessi granulari.
*   **✅ Workflow di Approvazione**: I contenuti (prodotti, eventi) passano attraverso un processo di validazione (Bozza -> In Revisione -> Approvato) gestito dai Curatori.
*   **🎟️ Eventi e Prenotazioni**: Gestione di eventi promozionali ed esperienziali sul territorio.

---

## 🛠️ Tecnologie Utilizzate

*   **Java 21** ☕
*   **Spring Boot 3** (Web, Data JPA, Security) 🍃
*   **MySQL** (Produzione e Sviluppo) 🗄️
*   **JWT** (JSON Web Token) per l'autenticazione sicura 🔐
*   **Maven** per la gestione delle dipendenze 📦
*   **Docker** (opzionale, per il deployment) 🐳

---

## ⚙️ Configurazione Database

L'applicazione richiede un database **MySQL** in esecuzione locale o remota.

1.  Assicurati che MySQL sia installato e avviato sul tuo sistema.
2.  Crea un database vuoto chiamato `filieraagricola`:
    ```sql
    CREATE DATABASE filieraagricola;
    ```
3.  L'applicazione si connette di default con identità `root`. Se la tua installazione richiede una password, puoi impostarla esportando la variabile d'ambiente `DB_PASSWORD` oppure modificando il file `src/main/resources/application.properties`:
    ```properties
    spring.datasource.password=la_tua_password
    ```
4.  Al primo avvio, Spring Boot creerà automaticamente tutte le tabelle nel database (`spring.jpa.hibernate.ddl-auto=update`).

---

## 🚀 Come Avviare il Progetto

### ☁️ Opzione Zero-Install (Consigliata)
Non hai Java o MySQL installati? Nessun problema! Usa **GitHub Codespaces**: un ambiente cloud completo e gratuito già configurato.

[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://codespaces.new/filippoian/FilieraAgricola-IDS2425)

1.  Clicca il badge qui sopra.
2.  Attendi il caricamento (ci vorrà qualche minuto la prima volta).
3.  Il sistema avvierà automaticamente il database e compilerà il progetto.
4.  Troverai l'applicazione pronta su una porta dedicata (es. 8080).

### 💻 Opzione Locale (Classica)
### Prerequisiti
*   JDK 21 installato.
*   Maven installato (o usa il wrapper `mvnw` incluso).
*   Un database MySQL in esecuzione e configurato come sopra.

### Istruzioni
1.  **Clona il repository**:
    ```bash
    git clone https://github.com/filippoian/FilieraAgricola-IDS2425.git
    cd FilieraAgricola-IDS2425
    ```

2.  **Compila il progetto**:
    ```bash
    mvn clean install
    ```

3.  **Avvia l'applicazione**:
    ```bash
    mvn spring-boot:run
    ```

L'applicazione sarà disponibile su `http://localhost:8080`.  
La **Mappa Interattiva** sarà visitabile a: `http://localhost:8080/mappa.html`.

---

## 📮 Testare le API con Postman

Nella cartella `docs/postman/` del progetto è disponibile una collection completa (`PopolamentoDatiCompleto.json`) già configurata per testare l'intera piattaforma (dalla registrazione all'approvazione prodotti).

### Come configurare Postman:
1.  **Importa la collection**: Apri Postman, clicca su "Import" (in alto a sinistra) e seleziona il file `docs/postman/PopolamentoDatiCompleto.json`.
2.  **Configura la variabile baseUrl**: Crea un "Environment" in Postman (o usa le variabili globali) e aggiungi una variabile chiamata `baseUrl` con valore `http://localhost:8080`.
3.  **Gestione dei Token JWT**: La collection include degli script (tab "Tests") che catturano automaticamente i token JWT ricevuti dopo un login e li salvano come variabili di ambiente (es. `gestore_token`, `produttore_token`, ecc.). Non avrai bisogno di copiarli manualmente per il continuo del flusso!
4.  **Flusso Consigliato**: Esegui le richieste nell'ordine numerato in cui sono elencate per mantenere la coerenza (partendo dalla registrazione degli utenti, passando per l'accreditamento con l'admin Gestore, fino ad arrivare alla creazione e approvazione dei prodotti).


---

## 🤝 Contribuire

Sentiti libero di aprire **Issues** o **Pull Requests** per migliorare il progetto!

---
*Progetto realizzato per il corso IDS 2025/2026 - Unicam* 🎓
