package it.unicam.cs.ids2425.FilieraAgricola.controller;

import it.unicam.cs.ids2425.FilieraAgricola.dto.request.AggiungiAlCarrelloRequest;
import it.unicam.cs.ids2425.FilieraAgricola.dto.response.CarrelloResponse;
import it.unicam.cs.ids2425.FilieraAgricola.service.CarrelloService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST per la gestione delle operazioni sul carrello.
 * Permette agli utenti di visualizzare, aggiungere e rimuovere articoli dal
 * proprio carrello.
 */
@RestController
@RequestMapping("/api/carrello")
@RequiredArgsConstructor
public class CarrelloController {

    private final CarrelloService carrelloService;

    /**
     * Recupera il carrello di un utente specifico.
     * Solo l'utente proprietario del carrello o un gestore possono accedere a
     * queste informazioni.
     *
     * @param utenteId ID dell'utente di cui recuperare il carrello.
     * @return Il contenuto del carrello.
     */
    @GetMapping("/{utenteId}")
    // Controllo di sicurezza: l'ID utente deve corrispondere a quello del token o
    // l'utente deve essere GESTORE
    @PreAuthorize("@customSecurityService.hasUserId(authentication, #utenteId) or hasRole('GESTORE')")
    public ResponseEntity<CarrelloResponse> getCarrello(@PathVariable Long utenteId) {
        return ResponseEntity.ok(carrelloService.getCarrello(utenteId));
    }

    /**
     * Aggiunge un prodotto al carrello dell'utente.
     * Operazione consentita solo all'utente proprietario del carrello.
     *
     * @param utenteId ID dell'utente.
     * @param request  Dati del prodotto da aggiungere (ID prodotto e quantità).
     * @return Il carrello aggiornato.
     */
    @PostMapping("/{utenteId}/aggiungi")
    // Controllo di sicurezza: solo l'utente stesso può modificare il proprio
    // carrello
    @PreAuthorize("@customSecurityService.hasUserId(authentication, #utenteId)")
    public ResponseEntity<CarrelloResponse> aggiungiProdotto(@PathVariable Long utenteId,
                                                             @RequestBody AggiungiAlCarrelloRequest request) {
        return ResponseEntity.ok(carrelloService.aggiungiProdotto(utenteId, request));
    }

    /**
     * Rimuove un prodotto specifico dal carrello dell'utente.
     * Operazione consentita solo all'utente proprietario del carrello.
     *
     * @param utenteId   ID dell'utente.
     * @param prodottoId ID del prodotto da rimuovere.
     * @return Il carrello aggiornato.
     */
    @DeleteMapping("/{utenteId}/rimuovi/{prodottoId}")
    // Controllo di sicurezza: solo l'utente stesso può modificare il proprio
    // carrello
    @PreAuthorize("@customSecurityService.hasUserId(authentication, #utenteId)")
    public ResponseEntity<CarrelloResponse> rimuoviProdotto(@PathVariable Long utenteId,
                                                            @PathVariable Long prodottoId) {
        return ResponseEntity.ok(carrelloService.rimuoviProdotto(utenteId, prodottoId));
    }
}