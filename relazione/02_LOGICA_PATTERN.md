# 02_LOGICA_PATTERN.md

## Design Patterns Utilizzati

Il "Core" dell'applicazione fa uso strategico di diversi pattern GoF per risolvere problemi specifici di manutenibilità e scalabilità del codice.

### 1. State Pattern
*   **Dove**: Package `it.unicam.cs.ids2425.FilieraAgricola.model`.
*   **Classi Coinvolte**:
    *   `ContentSubmission` (Context)
    *   `ContentState` (State Interface)
    *   `BozzaState`, `InRevisioneState`, `ApprovatoState`, `RifiutatoState` (Concrete States)
*   **Motivazione**: Gestire il complesso ciclo di vita dei contenuti (Prodotti, Eventi, Pacchetti) che devono essere approvati da un Curatore.
    *   *Perché è stato usato*: Per evitare una lunga serie di `if-else` o `switch` case sparsi nel Service layer. Ogni stato "sa" quali transizioni sono valide (es. non puoi approvare una Bozza se prima non è stata sottomessa). Questo incapsula la logica di transizione, rendendola robusta e facile da testare.

### 2. Strategy Pattern
*   **Dove**: Package `it.unicam.cs.ids2425.FilieraAgricola.service.pricing`.
*   **Classi Coinvolte**:
    *   `PricingStrategy` (Strategy Interface)
    *   `SingleItemPricingStrategy`, `PackagePricingStrategy` (Concrete Strategies)
    *   `OrdineService` (Context client)
*   **Motivazione**: Calcolare il prezzo di una riga d'ordine in modo diverso a seconda che si tratti di un prodotto singolo o di un pacchetto promozionale.
    *   *Perché è stato usato*: Per rispettare l'Open/Closed Principle. Se in futuro si volesse aggiungere una nuova logica di prezzo (es. "Sconto Quantità"), basterà aggiungere una nuova classe Strategy senza modificare il codice esistente del `OrdineService`.

### 3. Factory Pattern
*   **Dove**: Classe `it.unicam.cs.ids2425.FilieraAgricola.service.pricing.PricingStrategyFactory`.
*   **Motivazione**: Centralizzare la logica di creazione/selezione della Strategy corretta.
    *   *Perché è stato usato*: Il `OrdineService` non deve preoccuparsi di *come* istanziare la strategia giusta, ma solo di usarla. La Factory analizza l'oggetto `OrderLine` e restituisce l'algoritmo di prezzo idoneo, disaccoppiando la creazione dall'utilizzo.

### 4. Converter / Adapter Pattern (Logico)
*   **Dove**: Classe `it.unicam.cs.ids2425.FilieraAgricola.service.MappaService`.
*   **Motivazione**: Trasformare i dati del dominio (`FilieraPoint`) in un formato specifico per il frontend (GeoJSON).
    *   *Perché è stato usato*: Il metodo `convertiAPuntoGeoJSON` adatta l'interfaccia del modello interno allo standard GeoJSON richiesto dalle librerie di mappe (es. Leaflet/Mapbox), separando la rappresentazione persistente da quella di presentazione.

## Algoritmi e Logica Complessa

### 1. Algoritmo di Checkout e Storicizzazione Prezzi
*   **Dove**: `OrdineService.creaOrdineDaCheckout`
*   **Logica**:
    1.  Riceve una lista eterogenea di ID (Prodotti e Pacchetti).
    2.  Itera su ogni elemento (complessità **O(n)** dove n è il numero di item).
    3.  Per ogni elemento, recupera il prezzo *attuale* dal DB e crea una `OrderLine` che funge da *snapshot*. Questo è cruciale: se il prezzo del prodotto cambia domani, l'ordine storico deve mantenere il prezzo pagato oggi.
    4.  Delegando alla `PricingStrategyFactory`, calcola il subtotale.
    5.  Infine, esegue lo svuotamento automatico del carrello utente (`carrello.getArticoli().clear()`).

### 2. Ricorsione per Tracciabilità (Grafo dei Lotti)
*   **Dove**: `TraceabilityGraphDTO.buildRecursive` e `TracciabilitaService`.
*   **Logica**: Costruzione di un albero/grafo per visualizzare la storia di un prodotto trasformato.
    *   L'algoritmo è **ricorsivo** (DFS - Depth First Search). Partendo da un `ProductBatch` finale, risale la catena dei lotti di input (`batch.getLottiInput()`) per costruire l'intera genealogia del prodotto.
    *   *Complessità*: **O(V + E)** dove V sono i lotti e E sono le relazioni input/output. È essenziale per garantire la trasparenza completa della filiera "dal campo alla tavola".

### 3. Validazione composizionale dei Pacchetti
*   **Dove**: `PacchettoService.creaPacchetto` / `aggiornaPacchetto`
*   **Logica**:
    *   Un pacchetto è un aggregato di prodotti. La logica impone che non si possa inserire in un pacchetto un prodotto che non sia stato *già* approvato singolarmente (`StatoContenuto.APPROVATO`).
    *   In fase di aggiornamento, se un pacchetto (già approvato) viene modificato, il sistema invalida automaticamente il suo stato riportandolo in `BOZZA` (o `IN_REVISIONE`), forzando un nuovo ciclo di controllo qualità.

## Gestione della Concorrenza

Allo stato attuale, l'applicazione gestisce la concorrenza principalmente delegando al Container (Tomcat) e al Database.

*   **Transactional**: L'uso diffuso di `@Transactional` (es. in `OrdineService`, `CarrelloService`) garantisce che le operazioni complesse (come *crea ordine* + *svuota carrello*) siano atomiche. In caso di errore a metà processo, il DB esegue il rollback, prevenendo stati inconsistenti.
*   **Assenza di Async esplicito**: Non sono stati rilevati usi espliciti di `CompletableFuture`, `Thread` manuali o `@Async`. Il modello è sincrono bloccante (standard Servlet MVC), adeguato per le esigenze attuali di coerenza dei dati (ACID) prevalenti su quelle di throughput estremo.
