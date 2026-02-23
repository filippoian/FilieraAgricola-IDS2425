package it.unicam.cs.ids2425.FilieraAgricola.model;

/**
 * Enumerazione che definisce i ruoli autorizzativi gestiti dalla piattaforma.
 * Questi ruoli permettono il controllo granulare degli accessi e delle
 * funzionalità utente (es. Produttore, Distributore, ecc.).
 */
public enum ERole {
    ROLE_PRODUTTORE,
    ROLE_TRASFORMATORE,
    ROLE_DISTRIBUTORE,
    ROLE_CURATORE,
    ROLE_ANIMATORE,
    ROLE_ACQUIRENTE,
    ROLE_UTENTEGENERICO, // Ruolo di default
    ROLE_GESTORE // Ruolo admin
}
