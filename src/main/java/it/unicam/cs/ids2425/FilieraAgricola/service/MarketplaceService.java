package it.unicam.cs.ids2425.FilieraAgricola.service;

import it.unicam.cs.ids2425.FilieraAgricola.dto.request.MarketplaceItemRequest;
import it.unicam.cs.ids2425.FilieraAgricola.dto.response.MarketplaceItemResponse;
import it.unicam.cs.ids2425.FilieraAgricola.model.*;
import it.unicam.cs.ids2425.FilieraAgricola.repository.MarketplaceItemRepository;
import it.unicam.cs.ids2425.FilieraAgricola.repository.ProdottoRepository;
import it.unicam.cs.ids2425.FilieraAgricola.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service che gestisce le operazioni relative al Marketplace.
 * Si occupa della creazione di annunci di vendita e del recupero del catalogo.
 */
@Service
public class MarketplaceService {

    @Autowired
    private MarketplaceItemRepository marketplaceItemRepository;
    @Autowired
    private ProdottoRepository prodottoRepository;
    @Autowired
    private UtenteRepository utenteRepository;

    /**
     * Crea un nuovo annuncio di vendita nel marketplace.
     * Recupera l'utente autenticato, verifica che il prodotto esista, sia approvato
     * e appartenga all'utente corrente prima di creare l'item.
     *
     * @param request DTO contenente i dettagli del marketplace item da creare.
     * @return DTO contenente i dettagli dell'item creato.
     * @throws RuntimeException      se l'utente o il prodotto non vengono trovati.
     * @throws IllegalStateException se il prodotto non è approvato o non appartiene
     *                               all'utente.
     */
    @Transactional
    public MarketplaceItemResponse creaItem(MarketplaceItemRequest request) {
        // Recupera l'utente autenticato dal contesto di sicurezza
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        Utente venditore = utenteRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utente venditore non trovato"));

        // Recupera il prodotto specificato nella richiesta
        Prodotto prodotto = prodottoRepository.findById(request.getProdottoId())
                .orElseThrow(() -> new RuntimeException(
                        "Prodotto non trovato con id: " + request.getProdottoId()));

        // Verifica che il prodotto sia stato approvato dalla Curation
        if (prodotto.getSubmission() == null ||
                prodotto.getSubmission().getStatus() != StatoContenuto.APPROVATO) {
            throw new IllegalStateException(
                    "Impossibile vendere un prodotto che non è 'APPROVATO'. Stato attuale: " +
                            (prodotto.getSubmission() != null
                                    ? prodotto.getSubmission().getStatus()
                                    : "NON SOTTOMESSO"));
        }

        // Verifica che l'utente autenticato sia il proprietario del prodotto
        if (!prodotto.getUtente().getId().equals(venditore.getId())) {
            throw new IllegalStateException(
                    "Non hai i permessi per vendere un prodotto di un altro utente.");
        }

        // Crea e salva il nuovo MarketplaceItem
        MarketplaceItem item = new MarketplaceItem();
        item.setProdotto(prodotto);
        item.setVenditore(venditore);
        item.setPrezzoUnitario(request.getPrezzoUnitario());
        item.setUnitaDiMisura(request.getUnitaDiMisura());
        item.setStockDisponibile(request.getStockDisponibile());

        MarketplaceItem savedItem = marketplaceItemRepository.save(item);

        return new MarketplaceItemResponse(savedItem);
    }

    /**
     * Recupera il catalogo pubblico di tutti gli articoli in vendita.
     * Filtra gli articoli mostrando solo quelli il cui prodotto associato è nello
     * stato 'APPROVATO'.
     *
     * @return Lista di DTO rappresentanti gli articoli disponibili nel marketplace.
     */
    @Transactional(readOnly = true)
    public List<MarketplaceItemResponse> getCatalogo() {
        List<MarketplaceItem> items = marketplaceItemRepository.findAll();

        // Restituisce solo gli item il cui prodotto è approvato
        return items.stream()
                .filter(item -> item.getProdotto().getSubmission() != null &&
                        item.getProdotto().getSubmission()
                                .getStatus() == StatoContenuto.APPROVATO)
                .map(MarketplaceItemResponse::new)
                .collect(Collectors.toList());
    }
}