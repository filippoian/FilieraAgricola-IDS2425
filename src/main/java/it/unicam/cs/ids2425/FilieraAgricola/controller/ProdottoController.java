package it.unicam.cs.ids2425.FilieraAgricola.controller;

import it.unicam.cs.ids2425.FilieraAgricola.dto.request.ProdottoRequest;
import it.unicam.cs.ids2425.FilieraAgricola.dto.response.ProdottoResponse;
import it.unicam.cs.ids2425.FilieraAgricola.model.ContentSubmission;
import it.unicam.cs.ids2425.FilieraAgricola.service.CurationService;
import it.unicam.cs.ids2425.FilieraAgricola.service.ProdottoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST per la gestione dei Prodotti.
 * Espone gli endpoint per la creazione, consultazione e sottomissione dei
 * prodotti.
 */
@RestController
@RequestMapping("/api/prodotti")
@RequiredArgsConstructor
public class ProdottoController {

    private final ProdottoService prodottoService;
    private final CurationService curationService;

    /**
     * Endpoint per la creazione di un nuovo prodotto.
     * All'inizio il prodotto sarà nello stato BOZZA.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE')")
    public ResponseEntity<ProdottoResponse> creaProdotto(@RequestBody ProdottoRequest request) {
        return ResponseEntity.ok(prodottoService.creaProdotto(request));
    }

    /**
     * Endpoint usato dal proprietario per sottomettere il proprio prodotto (da
     * BOZZA a IN_REVISIONE).
     */
    @PostMapping("/{id}/sottometti")
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE')")
    public ResponseEntity<Void> sottomettiProdotto(@PathVariable Long id) {
        // Trova la sottomissione relativa a questo Prodotto
        ContentSubmission submission = curationService.findSubmissionByEntity(id, "PRODOTTO");
        // Esegue l'azione di sottomissione (passa in IN_REVISIONE)
        curationService.sottomettiContenuto(submission.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * Ritorna tutti i prodotti APPROVATI (visibili pubblicamente).
     */
    @GetMapping
    public ResponseEntity<List<ProdottoResponse>> getAllProdotti() {
        return ResponseEntity.ok(prodottoService.getAllProdotti());
    }

    /**
     * Ritorna i dettagli di un singolo prodotto tramite ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProdottoResponse> getProdottoById(@PathVariable Long id) {
        return ResponseEntity.ok(prodottoService.getProdottoById(id));
    }

    /**
     * Ritorna i prodotti associati a un utente specifico.
     */
    @GetMapping("/utente/{utenteId}")
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE', 'GESTORE', 'CURATORE')")
    public ResponseEntity<List<ProdottoResponse>> getProdottiByUtente(@PathVariable Long utenteId) {
        return ResponseEntity.ok(prodottoService.getProdottiByUtente(utenteId));
    }

    /**
     * Aggiorna le informazioni di un prodotto.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE')")
    public ResponseEntity<ProdottoResponse> aggiornaProdotto(@PathVariable Long id,
            @RequestBody ProdottoRequest request) {
        return ResponseEntity.ok(prodottoService.aggiornaProdotto(id, request));
    }

    /**
     * Elimina un prodotto.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE', 'GESTORE', 'CURATORE')")
    public ResponseEntity<Void> eliminaProdotto(@PathVariable Long id) {
        prodottoService.eliminaProdotto(id);
        return ResponseEntity.ok().build();
    }
}
