# 03_DATI_INTERFACCE.md

## Modello Dati (ER)

Il sistema utilizza JPA (Java Persistence API) su Hibernate per mappare gli oggetti Java su un database relazionale (MySQL).

### Entità Principali

1.  **Utente (`utenti`)**
    *   Rappresenta un attore del sistema.
    *   **Relazioni**:
        *   `@ManyToMany` con `Role`: Un utente ha uno o più ruoli (es. PRODUTTORE + VENDITORE).
        *   `@OneToOne` con `UserProfile`: Profilo esteso (Nome, Cognome, Telefono).
        *   `@OneToOne` con `Carrello`: Ogni utente possiede un carrello personale.
    *   *Annotazioni Chiave*: `fetch = FetchType.EAGER` sui ruoli per averli sempre disponibili nel Security Context.

2.  **ProductBatch (`product_batch`)**
    *   Rappresenta un lotto di produzione fisico (es. "Lotto 123 di Marmellata").
    *   **Relazioni**:
        *   `@ManyToOne` con `Prodotto`: Il tipo di prodotto (es. "Marmellata di Fragole").
        *   `@OneToMany` con `TraceabilityStep`: Lista cronologica delle azioni subite dal lotto.
        *   `@OneToMany` (Autoreferenziale) tramite `BatchInputLink`: Un lotto può essere composto da altri lotti (es. Marmellata fatta con Lotto Zucchero + Lotto Fragole).
    *   *Annotazioni Chiave*: `@EqualsAndHashCode.Exclude` sulle liste per evitare ricorsioni infinite di Lombok.

3.  **MarketplaceItem (`marketplace_item`)**
    *   Rappresenta un'inserzione di vendita.
    *   **Relazioni**:
        *   `@ManyToOne` con `Prodotto`.
        *   `@ManyToOne` con `Utente` (Venditore).
    *   *Campi*: Prezzo, Stock, Unità di Misura.

4.  **ContentSubmission (`content_submission`)**
    *   Entità polimorfica per la gestione delle approvazioni (State Pattern).
    *   *Campi*: `submittableEntityId`, `submittableEntityType`, `status` (`BOZZA`, `IN_REVISIONE`, ecc.).
    *   Non ha chiavi esterne fisiche (Join logico applicativo) per massima flessibilità.

## Strutture Dati

Il sistema privilegia l'uso di **DTO (Data Transfer Objects)** per il passaggio dati, mantenendo le Entity confinate al layer di persistenza.

### DTO Critici
*   **Request DTOs**:
    *   `LottoCreateDTO`: Contiene `productId`, `quantita`, e una `List<Long> inputBatchIds` per definire la genealogia del lotto.
    *   `CarrelloCheckoutDTO`: Struttura complessa con due liste separate: `items` (prodotti singoli) e `pacchetti`.
*   **Response DTOs**:
    *   `TraceabilityGraphDTO`: Struttura ad albero (ricorsiva) che mappa l'intera filiera. Contiene un `Set<TraceabilityGraphDTO> inputBatches` per navigare a ritroso verso le materie prime.
    *   `GeoJSONFeatureCollection`: Struttura Map-like (`Map<String, Object> properties`) conforme allo standard RFC 7946 per la visualizzazione su mappa.

## API / Interfacce

L'applicazione espone una **REST API** documentata e sicura.

### Endpoint Principali

1.  **Tracciabilità** (`/api/tracciabilita`)
    *   `POST /lotti`: Crea un nuovo lotto (Produttore/Trasformatore).
    *   `POST /lotti/{id}/fasi`: Aggiunge un evento al diario del lotto (es. "Raccolto", "Trasformato").
    *   `GET /lotti/{id}/storia`: Restituisce il grafo completo della filiera (Pubblico).

2.  **Ordini & Checkout** (`/api/ordini`)
    *   `POST /checkout/{utenteId}`: Trasforma il carrello in un ordine.
    *   `GET /utente/{id}`: Storico ordini.

3.  **Marketplace** (`/api/marketplace`)
    *   `GET /catalogo`: Ricerca prodotti filtri.
    *   `GET /item/{id}`: Dettaglio articolo.

4.  **Curation** (`/api/curation`)
    *   `GET /revisione`: Lista contenuti pendenti (Solo Curatori).
    *   `POST /{id}/approva`: Approva un contenuto.

## Flusso Dati: "Creazione Lotto con Tracciabilità"

Descrizione del viaggio del dato per il caso d'uso: **Creazione di un Lotto Trasformato (es. Salsa)**.

1.  **Frontend/Client**: Invia un JSON `POST /api/tracciabilita/lotti` con:
    *   `productId`: ID del prodotto "Salsa".
    *   `inputBatchIds`: [10, 11] (ID dei lotti "Pomodori" e "Sale").
2.  **Controller**: `TracciabilitaController` intercetta la richiesta, verifica il token JWT (Ruolo PRODUTTORE/TRASFORMATORE) e deserializza in `LottoCreateDTO`.
3.  **Service**: `TracciabilitaService.creaLotto`
    *   Recupera le entity dei lotti input (10, 11) dal DB.
    *   Crea la nuova entity `ProductBatch` per la Salsa.
    *   Crea le entity di collegamento `BatchInputLink` che uniscono Salsa -> Pomodori e Salsa -> Sale.
4.  **Repository/Hibernate**:
    *   `batchRepository.save(newBatch)`: INSERT nella tabella `product_batch`.
    *   `batchInputLinkRepository.save(link)`: INSERT nella tabella di join `batch_input_link`.
5.  **Database**: I dati sono persistiti con vincoli di integrità referenziale.
6.  **Ritorno**: Il Service restituisce l'oggetto salvato, il Controller lo serializza in JSON e lo invia al Client con HTTP 200.
