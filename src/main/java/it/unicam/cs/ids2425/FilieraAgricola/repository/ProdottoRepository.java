package it.unicam.cs.ids2425.FilieraAgricola.repository;

import it.unicam.cs.ids2425.FilieraAgricola.model.Prodotto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository per la gestione del catalogo prodotti.
 *
 * Questa interfaccia gestisce la persistenza delle definizioni dei prodotti
 * (es. "Mela Golden", "Formaggio Pecorino").
 * I prodotti rappresentano le tipologie di beni che gli attori della filiera
 * possono produrre o trasformare,
 * fungendo da "template" per la creazione dei lotti specifici.
 */
public interface ProdottoRepository extends JpaRepository<Prodotto, Long> {

    /**
     * Recupera la lista di tutti i prodotti definiti da un determinato utente.
     *
     * Questo metodo e' essenziale per la dashboard del produttore o trasformatore,
     * permettendo loro di visualizzare e gestire il proprio catalogo personale di
     * prodotti.
     *
     * @param utenteId L'ID dell'utente proprietario dei prodotti.
     * @return Una lista di oggetti Prodotto creati dall'utente specificato.
     */
    List<Prodotto> findByUtenteId(Long utenteId);
}
