# 01_ARCHITETTURA.md

## Scopo del Progetto
**FilieraAgricola** è una piattaforma software progettata per la gestione integrata della filiera agroalimentare. Il sistema funge da intermediario digitale tra i vari attori della filiera (Produttori, Trasformatori, Distributori, Acquirenti), permettendo la gestione del catalogo prodotti, la vendita tramite e-commerce (Carrello/Checkout), la tracciabilità delle merci e la curatela dei contenuti (approvazione di prodotti ed eventi) per garantire qualità e trasparenza verso il consumatore finale.

## Stack Tecnologico

### Core Framework e Linguaggio
*   **Java**: Versione 21 (LTS).
*   **Spring Boot**: Versione 3.4.5.

### Moduli Spring Utilizzati
*   **Spring Web**: Per la creazione di API RESTful e server Tomcat embedded.
*   **Spring Data JPA**: Per l'astrazione e la persistenza dei dati (basato su Hibernate).
*   **Spring Security**: Per l'autenticazione e il controllo degli accessi.

### Database e Persistenza
*   **MySQL**: RDBMS utilizzato come store principale.
*   **MySQL Connector/J**: Driver JDBC.
*   **H2 Database**: Utilizzato in scope `test` per database in-memory.

### Sicurezza e Autenticazione
*   **JWT (JSON Web Token)**: Implementato tramite le librerie `io.jsonwebtoken` (versione 0.11.5) per autenticazione stateless.
*   **BCrypt**: Per l'hashing sicuro delle password.

### Utility e Build Tools
*   **Maven**: Build automation tool e gestione delle dipendenze.
*   **Lombok** (v1.18.36): Per la riduzione del codice boilerplate (getter, setter, costruttori).
*   **Jackson**: Per la serializzazione/deserializzazione JSON (incluso via `jjwt-jackson`).

## Struttura del Progetto

Il codice sorgente è organizzato secondo una **Layered Architecture (Architettura a Strati)** classica, tipica dello standard Spring Boot, che favorisce la separazione delle responsabilità (SoC).

### Organizzazione dei Package (`it.unicam.cs.ids2425.FilieraAgricola`)

*   **`config`**: Contiene le classi di configurazione Spring (es. `SecurityConfig` per i filtri e le regole di sicurezza).
*   **`controller` (Presentation Layer)**: Gestisce le richieste HTTP in ingresso. I controller sono mappati sulle risorse (es. `OrdineController`, `MarketplaceController`) e fungono da entry-point, occupandosi della validazione input e della conversione in DTO.
*   **`service` (Business Layer)**: Contiene la logica di business applicativa. I service (es. `OrdineService`) orchestrano le operazioni, gestiscono le transazioni (`@Transactional`) e implementano regole complesse (es. calcolo prezzi tramite Strategy Pattern).
*   **`repository` (Data Access Layer)**: Interfacce che estendono `JpaRepository`, fornendo metodi CRUD e query personalizzate verso il database.
*   **`model` (Persistence Layer)**: Definisce le Entity JPA che mappano le tabelle del database (es. `Utente`, `Ordine`, `Prodotto`).
*   **`dto` (Data Transfer Objects)**: Oggetti poveri di logica usati esclusivamente per trasferire dati tra frontend e backend, disaccoppiando il modello interno dalle API pubbliche.
*   **`security`**: Implementazione specifica per JWT (`JwtAuthFilter`, `JwtUtils`) e dettagli utente (`UserDetailsImpl`).
*   **`exception`**: Gestione centralizzata degli errori tramite `GlobalExceptionHandler`.

## Diagramma Concettuale

Il sistema opera secondo un flusso di richiesta-risposta sincrono:

1.  **Ingresso Richiesta**: Il Client invia una richiesta HTTP REST (spesso con Header `Authorization: Bearer <token>`).
2.  **Filtro di Sicurezza**: La `SecurityFilterChain` intercetta la richiesta. Il `JwtAuthFilter` valida il token e popola il `SecurityContext` con l'identità dell'utente.
3.  **Routing e Validazione**: La richiesta raggiunge il **Controller** appropriato, che deserializza il JSON nei DTO di richiesta e valida i campi.
4.  **Elaborazione (Business Logic)**: Il Controller invoca il **Service**. Il Service esegue la logica di dominio (es. verifica disponibilità prodotto, cambio stato ordine), applicando regole di coerenza.
5.  **Accesso ai Dati**: Se necessario, il Service interroga il **Repository**. Il Repository traduce le chiamate in query SQL verso il Database MySQL tramite Hibernate.
6.  **Risposta**: I dati recuperati/modificati risalgono la catena, vengono trasformati in DTO di risposta e serializzati in JSON per essere restituiti al Client con il codice HTTP appropriato (200 OK, 400 Bad Request, ecc.).
