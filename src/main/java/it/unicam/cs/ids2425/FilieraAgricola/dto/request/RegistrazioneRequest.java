package it.unicam.cs.ids2425.FilieraAgricola.dto.request;

import it.unicam.cs.ids2425.FilieraAgricola.model.ERole;
import lombok.Data;

/**
 * DTO (Data Transfer Object) utilizzato per gestire le richieste di
 * registrazione di nuovi utenti.
 * <p>
 * Questa classe raccoglie tutte le informazioni necessarie per creare un nuovo
 * account utente,
 * incluse le generalità, i contatti, le credenziali di sicurezza e il ruolo
 * richiesto all'interno della filiera.
 */
@Data
public class RegistrazioneRequest {

    /**
     * Il nome di battesimo dell'utente.
     */
    private String nome;

    /**
     * Il cognome dell'utente.
     */
    private String cognome;

    /**
     * Il numero di telefono dell'utente, utile per eventuali contatti o verifiche.
     */
    private String telefono;

    /**
     * L'indirizzo email dell'utente.
     * Viene utilizzato anche come identificativo univoco (username) per l'accesso
     * al sistema.
     */
    private String email;

    /**
     * La password scelta dall'utente per proteggere il proprio account.
     * Dovrebbe rispettare i criteri di sicurezza definiti dal sistema.
     */
    private String password;

    /**
     * Il ruolo che l'utente intende assumere all'interno della piattaforma
     * (es. PRODUTTORE, TRASFORMATORE, DISTRIBUTORE, CURATORE, ANIMATORE,
     * ACQUIRENTE).
     */
    private ERole ruolo;
}