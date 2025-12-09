package it.unicam.cs.ids2425.FilieraAgricola.dto.request;

import lombok.Data;

/**
 * DTO (Data Transfer Object) utilizzato per le richieste di autenticazione
 * (login).
 * <p>
 * Contiene le credenziali necessarie (email e password) per permettere a un
 * utente
 * di accedere al sistema e ottenere un token di sicurezza.
 */
@Data
public class LoginRequest {

    /**
     * L'indirizzo email dell'utente, utilizzato come username per l'autenticazione.
     */
    private String email;

    /**
     * La password dell'account utente.
     */
    private String password;
}
