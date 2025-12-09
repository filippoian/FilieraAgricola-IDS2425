package it.unicam.cs.ids2425.FilieraAgricola.dto.response;

import it.unicam.cs.ids2425.FilieraAgricola.model.Utente;
import lombok.Data;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO (Data Transfer Object) utilizzato per inviare la risposta al client in
 * seguito
 * a un'autenticazione avvenuta con successo.
 * <p>
 * Contiene il token JWT per le successive richieste autenticate e le
 * informazioni
 * principali dell'utente loggato.
 */
@Data
public class LoginResponse {

    /**
     * Il token JWT (JSON Web Token) generato per la sessione corrente.
     * Deve essere incluso nell'header delle richieste successive per
     * l'autorizzazione.
     */
    private String token;

    /**
     * L'identificativo univoco dell'utente nel sistema.
     */
    private Long id;

    /**
     * Il nome dell'utente.
     */
    private String nome;

    /**
     * Il cognome dell'utente.
     */
    private String cognome;

    /**
     * L'indirizzo email dell'utente.
     */
    private String email;

    /**
     * La lista dei ruoli assegnati all'utente (es. ROLE_PRODUTTORE,
     * ROLE_ACQUIRENTE).
     */
    private List<String> ruoli;

    /**
     * Costruisce una nuova risposta di login popolando i campi con i dati
     * dell'utente fornito.
     *
     * @param token  Il token JWT generato.
     * @param utente L'entità {@link Utente} che ha effettuato l'accesso.
     */
    public LoginResponse(String token, Utente utente) {
        this.token = token;
        this.id = utente.getId();
        this.email = utente.getEmail();

        // Estrae i dati anagrafici dal profilo collegato
        if (utente.getUserProfile() != null) {
            this.nome = utente.getUserProfile().getNome();
            this.cognome = utente.getUserProfile().getCognome();
        }

        this.ruoli = utente.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());
    }
}