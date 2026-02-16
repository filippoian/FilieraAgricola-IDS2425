package it.unicam.cs.ids2425.FilieraAgricola.controller;

import it.unicam.cs.ids2425.FilieraAgricola.dto.request.CarrelloCheckoutDTO;

import it.unicam.cs.ids2425.FilieraAgricola.dto.response.OrdineResponse;
import it.unicam.cs.ids2425.FilieraAgricola.service.OrdineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST per la gestione delle operazioni relative agli ordini.
 * <p>
 * Espone endpoint per:
 * <ul>
 * <li>Creare un ordine a partire da un carrello (Checkout).</li>
 * <li>Visualizzare lo storico ordini di un utente.</li>
 * <li>Aggiornare lo stato di un ordine (operazione gestionale).</li>
 * </ul>
 * <p>
 * Utilizza annotazioni {@code @PreAuthorize} per garantire l'accesso solo agli
 * utenti autorizzati.
 */
@RestController
@RequestMapping("/api/ordini")
@RequiredArgsConstructor
public class OrdineController {

    private final OrdineService ordineService;

    /**
     * Endpoint per il processo di Checkout.
     * <p>
     * Riceve il contenuto del carrello e avvia la creazione dell'ordine.
     * L'accesso è consentito solo all'acquirente proprietario del carrello
     * (verifica su {@code utenteId}).
     *
     * @param utenteId ID dell'utente che effettua l'acquisto.
     * @param request  DTO contenente i dettagli degli articoli/pacchetti nel
     *                 carrello.
     * @return La risposta con i dettagli dell'ordine creato.
     */
    @PostMapping("/checkout/{utenteId}")
    @PreAuthorize("hasRole('ACQUIRENTE') and #utenteId == authentication.principal.id")
    public ResponseEntity<OrdineResponse> creaOrdine(
            @PathVariable Long utenteId,
            @RequestBody CarrelloCheckoutDTO request) {

        return ResponseEntity.ok(ordineService.creaOrdineDaCheckout(utenteId, request));
    }

    /**
     * Recupera la lista degli ordini effettuati da uno specifico utente.
     * <p>
     * Accessibile solo dall'utente stesso.
     *
     * @param id ID dell'acquirente.
     * @return Lista di DTO {@link OrdineResponse}.
     */
    @GetMapping("/utente/{id}")
    @PreAuthorize("hasRole('ACQUIRENTE') and #id == authentication.principal.id")
    public ResponseEntity<List<OrdineResponse>> getOrdiniByUtente(@PathVariable Long id) {
        return ResponseEntity.ok(ordineService.getOrdiniByUtente(id));
    }

    /**
     * Aggiorna lo stato di avanzamento di un ordine (es. da PAGATO a SPEDITO).
     * <p>
     * Operazione riservata ai ruoli gestionali o ai venditori coinvolti nella
     * filiera
     * (Gestore, Produttore, Trasformatore, Distributore).
     *
     * @param id    ID dell'ordine da aggiornare.
     * @param stato Nuovo stato da assegnare (stringa corrispondente all'enum).
     * @return Messaggio di conferma.
     */
    @PostMapping("/{id}/stato")
    @PreAuthorize("hasRole('GESTORE') or hasAnyRole('PRODUTTORE', 'TRASFORMATORE', 'DISTRIBUTORE')")
    public ResponseEntity<String> aggiornaStatoOrdine(
            @PathVariable Long id,
            @RequestParam String stato) {
        ordineService.aggiornaStato(id, stato);
        return ResponseEntity.ok("Stato aggiornato con successo");
    }
}