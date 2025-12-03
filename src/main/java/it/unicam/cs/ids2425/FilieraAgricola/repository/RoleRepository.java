package it.unicam.cs.ids2425.FilieraAgricola.repository;

import it.unicam.cs.ids2425.FilieraAgricola.model.ERole;
import it.unicam.cs.ids2425.FilieraAgricola.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository per la gestione della persistenza dei ruoli utente.
 *
 * Questa interfaccia permette di interagire con la tabella dei ruoli nel
 * database,
 * fornendo meccanismi per recuperare le definizioni dei ruoli (es. ADMIN,
 * PRODUTTORE)
 * necessarie per la gestione dei permessi e delle autorizzazioni.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    /**
     * Recupera un'entita' Ruolo basandosi sul suo nome enumerato.
     *
     * Questo metodo e' fondamentale durante la registrazione o l'assegnazione dei
     * permessi,
     * permettendo di convertire un valore dell'enum ERole nella corrispondente
     * entita' persistente
     * da associare all'utente.
     *
     * @param name Il valore dell'enum che identifica il ruolo cercato.
     * @return Un Optional contenente il Ruolo se esiste nel database, altrimenti
     *         vuoto.
     */
    Optional<Role> findByName(ERole name);
}