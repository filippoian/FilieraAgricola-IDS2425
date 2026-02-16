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
*   **🗺️ Mappa Interattiva**: Visualizzazione GeoJSON dei punti della filiera (Aziende, Trasformatori, Distributori).
*   **👥 Gestione Ruoli Avanzata**: Sistema multi-ruolo (Produttore, Trasformatore, Distributore, Curatore, Gestore, Acquirente) con permessi granulari.
*   **✅ Workflow di Approvazione**: I contenuti (prodotti, eventi) passano attraverso un processo di validazione (Bozza -> In Revisione -> Approvato) gestito dai Curatori.
*   **🎟️ Eventi e Prenotazioni**: Gestione di eventi promozionali ed esperienziali sul territorio.

---

## 🛠️ Tecnologie Utilizzate

*   **Java 21** ☕
*   **Spring Boot 3** (Web, Data JPA, Security) 🍃
*   **MySQL** (Produzione) / **H2** (Testing e Sviluppo rapido) 🗄️
*   **JWT** (JSON Web Token) per l'autenticazione sicura 🔐
*   **Maven** per la gestione delle dipendenze 📦
*   **Docker** (opzionale, per il deployment) 🐳

---

## 🚀 Come Avviare il Progetto

### Prerequisiti
*   JDK 21 installato.
*   Maven installato (o usa il wrapper `mvnw` incluso).
*   Un database MySQL in esecuzione (o configura `application.properties` per usare H2 in memoria).

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

---

## 🧪 Testing

Per eseguire i test unitari e di integrazione (che usano il database in-memory H2):

```bash
mvn test
```

---

## 🤝 Contribuire

Sentiti libero di aprire **Issues** o **Pull Requests** per migliorare il progetto!

---
*Progetto realizzato per il corso IDS 2025/2026 - Unicam* 🎓
