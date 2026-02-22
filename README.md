# 🚜 Filiera Agricola

Benvenuto nella **Piattaforma di Filiera Agricola**! 🌱
Questo progetto è un sistema backend basato su **Spring Boot** per la gestione, tracciabilità e vendita di prodotti agroalimentari, garantendo trasparenza dal produttore al consumatore.

---

## 📚 Documentazione

La documentazione del progetto è divisa in due parti:

1.  **Relazione di Ingegneria del Software**: Il documento principale con l'analisi dei requisiti, le scelte architetturali e i pattern utilizzati è disponibile nella cartella del progetto in formato PDF:  
    👉 **[Visualizza Relazione Ingegneria del Software](docs/Relazione%20Ingegneria%20del%20Software.pdf)**

2.  **Documentazione Tecnica (Javadoc)**: La documentazione generata per le API e le classi del backend è disponibile online qui:  
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
Non hai Java o MySQL installati? Nessun problema! Usa **GitHub Codespaces**: un ambiente cloud completo e gratuito, configurato nativamente con **Docker Compose** per darti Java 21 e MySQL 8 pronti all'uso in pochi istanti.

[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://codespaces.new/filippoian/FilieraAgricola-IDS2425)

1.  Clicca il badge qui sopra e seleziona **Create codespace on main** (se ne avevi uno vecchio, eliminalo e ricrealo).
2.  Attendi l'apertura dell'editor nel browser. Il database MySQL si configurerà automaticamente in background.
3.  Apri un **Terminale** (`Terminal -> New Terminal`) nell'editor di Codespaces.
4.  Scarica le dipendenze e compila il progetto saltando i test (necessario al primo avvio):
    ```bash
    mvn clean install -DskipTests
    ```
5.  Avvia il server Spring Boot:
    ```bash
    mvn spring-boot:run
    ```
6.  In basso a destra comparirà un pop-up: clicca su **Open in Browser** per visualizzare l'app.

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
2.  **Configura la variabile baseUrl**: 
    *   **Se usi l'ambiente Locale**: Crea un "Environment" (o usa variabili globali) e aggiungi una variabile `baseUrl` con valore `http://localhost:8080`.
    *   **Se usi GitHub Codespaces**: 
        1. Su Codespaces, in basso vai nel tab **Ports** (vicino al Terminale).
        2. Clicca col tasto destro sulla porta `8080` e imposta **"Port Visibility" su "Public"** (fondamentale, altrimenti Postman verrà bloccato!).
        3. Fai nuovamente tasto destro e seleziona **"Copy Forwarded Address"** (avrà un formato simile a: `https://nome-random-8080.app.github.dev`).
        4. Incolla questo link in Postman come valore della variabile `baseUrl` (**senza lo slash `/` finale**).
3.  **Gestione dei Token JWT**: La collection include degli script (tab "Tests") che catturano automaticamente i token JWT ricevuti dopo un login e li salvano come variabili di ambiente. Non avrai bisogno di copiarli manualmente!
4.  **Flusso Consigliato**: Esegui le richieste rigorosamente nell'ordine in cui sono elencate per mantenere la coerenza (partendo dalla registrazione utenti).


---

## 🤝 Contribuire

Sentiti libero di aprire **Issues** o **Pull Requests** per migliorare il progetto!

---
*Progetto realizzato per il corso IDS 2025/2026 - Unicam* 🎓
