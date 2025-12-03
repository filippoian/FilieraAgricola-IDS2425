package it.unicam.cs.ids2425.FilieraAgricola.repository;

import it.unicam.cs.ids2425.FilieraAgricola.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository per la gestione della persistenza delle entita' Utente.
 * Fornisce i metodi CRUD standard ereditati da JpaRepository e query
 * personalizzate
 * per l'accesso ai dati degli utenti nel database.
 */
public interface UtenteRepository extends JpaRepository<Utente, Long> {

    /**
     * Cerca un utente specifico utilizzando il suo indirizzo email.
     * Questo metodo e' utile per le operazioni di login o per verificare
     * l'esistenza
     * di un utente durante la registrazione.
     *
     * @param email L'indirizzo email dell'utente da cercare.
     * @return Un Optional contenente l'Utente se trovato, altrimenti un Optional
     *         vuoto.
     */
    Optional<Utente> findByEmail(String email);
}
