package it.unicam.cs.ids2425.FilieraAgricola.repository;

import it.unicam.cs.ids2425.FilieraAgricola.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository per la gestione dei profili utente estesi.
 *
 * Questa interfaccia gestisce l'entita' UserProfile, che contiene informazioni
 * aggiuntive
 * rispetto all'account base (Utente). E' fondamentale per collegare i dati di
 * login
 * ai ruoli specifici della filiera (es. Produttore, Distributore) e ai dati
 * anagrafici completi.
 */
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    /**
     * Cerca il profilo dettagliato associato a un determinato ID utente.
     *
     * Questo metodo permette di recuperare le informazioni estese (es. nome,
     * cognome, indirizzo)
     * partendo dall'identificativo dell'account di login. E' utilizzato spesso dai
     * servizi
     * di gestione per verificare lo stato di completamento del profilo o per
     * visualizzare
     * i dati nella dashboard personale.
     *
     * @param utenteId L'ID univoco dell'utente (account) di cui si cerca il
     *                 profilo.
     * @return Un Optional contenente il UserProfile se l'associazione esiste,
     *         altrimenti vuoto.
     */
    Optional<UserProfile> findByUtenteId(Long utenteId);
}