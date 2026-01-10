package it.unicam.cs.ids2425.FilieraAgricola.service;

import it.unicam.cs.ids2425.FilieraAgricola.dto.request.AggiungiAlCarrelloRequest;
import it.unicam.cs.ids2425.FilieraAgricola.dto.response.ArticoloCarrelloResponse;
import it.unicam.cs.ids2425.FilieraAgricola.dto.response.CarrelloResponse;
import it.unicam.cs.ids2425.FilieraAgricola.model.ArticoloCarrello;
import it.unicam.cs.ids2425.FilieraAgricola.model.Carrello;
import it.unicam.cs.ids2425.FilieraAgricola.model.Prodotto;
import it.unicam.cs.ids2425.FilieraAgricola.model.Utente;
import it.unicam.cs.ids2425.FilieraAgricola.repository.CarrelloRepository;
import it.unicam.cs.ids2425.FilieraAgricola.repository.ProdottoRepository;
import it.unicam.cs.ids2425.FilieraAgricola.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service per la gestione del carrello acquisti degli utenti.
 * Gestisce l'aggiunta, la rimozione e il recupero dei prodotti nel carrello.
 */
@Service
@RequiredArgsConstructor
public class CarrelloService {

    private final CarrelloRepository carrelloRepository;
    private final ProdottoRepository prodottoRepository;
    private final UtenteRepository utenteRepository;

    /**
     * Recupera il carrello di un utente specifico.
     * Se il carrello non esiste, ne crea uno nuovo associato all'utente.
     *
     * @param utenteId ID dell'utente proprietario del carrello.
     * @return Il carrello esistente o appena creato.
     * @throws RuntimeException se l'utente non viene trovato.
     */
    private Carrello getOrCreateCarrello(Long utenteId) {
        return carrelloRepository.findByUtenteId(utenteId)
                .orElseGet(() -> {
                    // Recupera l'utente per associarlo al nuovo carrello
                    Utente utente = utenteRepository.findById(utenteId)
                            .orElseThrow(() -> new RuntimeException("Utente non trovato con id: " + utenteId));
                    Carrello nuovoCarrello = new Carrello(utente);
                    return carrelloRepository.save(nuovoCarrello);
                });
    }

    /**
     * Aggiunge un prodotto al carrello dell'utente.
     * Se il prodotto è già presente, ne aggiorna la quantità.
     *
     * @param utenteId ID dell'utente.
     * @param request  DTO contenente ID prodotto e quantità da aggiungere.
     * @return DTO aggiornato dello stato del carrello.
     */
    @Transactional
    public CarrelloResponse aggiungiProdotto(Long utenteId, AggiungiAlCarrelloRequest request) {
        // Recupera o crea il carrello per l'utente
        Carrello carrello = getOrCreateCarrello(utenteId);

        // Verifica l'esistenza del prodotto
        Prodotto prodotto = prodottoRepository.findById(request.getProdottoId())
                .orElseThrow(() -> new RuntimeException("Prodotto non trovato con id: " + request.getProdottoId()));

        // Cerca se il prodotto è già nel carrello
        Optional<ArticoloCarrello> articoloEsistente = carrello.getArticoli().stream()
                .filter(art -> art.getProdotto().getId().equals(prodotto.getId()))
                .findFirst();

        if (articoloEsistente.isPresent()) {
            // Se esiste, incrementa la quantità
            ArticoloCarrello articolo = articoloEsistente.get();
            articolo.setQuantita(articolo.getQuantita() + request.getQuantita());
        } else {
            // Se non esiste, crea un nuovo articolo nel carrello
            ArticoloCarrello nuovoArticolo = new ArticoloCarrello(carrello, prodotto, request.getQuantita());
            carrello.getArticoli().add(nuovoArticolo);
        }

        // Salva le modifiche
        carrelloRepository.save(carrello);
        return toResponse(carrello);
    }

    /**
     * Rimuove un prodotto dal carrello dell'utente.
     *
     * @param utenteId   ID dell'utente.
     * @param prodottoId ID del prodotto da rimuovere.
     * @return DTO aggiornato dello stato del carrello.
     */
    @Transactional
    public CarrelloResponse rimuoviProdotto(Long utenteId, Long prodottoId) {
        Carrello carrello = getOrCreateCarrello(utenteId);

        // Rimuove l'articolo corrispondente al prodotto specificato
        carrello.getArticoli().removeIf(articolo -> articolo.getProdotto().getId().equals(prodottoId));

        carrelloRepository.save(carrello);
        return toResponse(carrello);
    }

    /**
     * Ottiene lo stato attuale del carrello di un utente.
     *
     * @param utenteId ID dell'utente.
     * @return DTO rappresentante il carrello e il suo contenuto.
     */
    public CarrelloResponse getCarrello(Long utenteId) {
        Carrello carrello = getOrCreateCarrello(utenteId);
        return toResponse(carrello);
    }

    /**
     * Metodo di utilità per convertire una entity Carrello in un DTO
     * CarrelloResponse.
     *
     * @param carrello Entity del carrello da convertire.
     * @return DTO contenente i dati del carrello e la lista degli articoli.
     */
    private CarrelloResponse toResponse(Carrello carrello) {
        List<ArticoloCarrelloResponse> articoliResponse = carrello.getArticoli().stream()
                .map(art -> new ArticoloCarrelloResponse(
                        art.getProdotto().getId(),
                        art.getProdotto().getNome(),
                        art.getQuantita()))
                .collect(Collectors.toList());
        return new CarrelloResponse(carrello.getId(), carrello.getUtente().getId(), articoliResponse);
    }
}