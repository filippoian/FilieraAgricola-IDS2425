package it.unicam.cs.ids2425.FilieraAgricola.controller;

import it.unicam.cs.ids2425.FilieraAgricola.dto.request.MarketplaceItemRequest;
import it.unicam.cs.ids2425.FilieraAgricola.dto.response.MarketplaceItemResponse;
import it.unicam.cs.ids2425.FilieraAgricola.service.MarketplaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST per la gestione del Marketplace.
 * Fornisce endpoint per la vendita di prodotti e la consultazione del catalogo.
 */
@RestController
@RequestMapping("/api/marketplace")
public class MarketplaceController {

    @Autowired
    private MarketplaceService marketplaceService;

    /**
     * Endpoint per mettere in vendita un nuovo articolo sul marketplace.
     * L'operazione è consentita solo agli utenti con ruoli professionali
     * (PRODUTTORE, TRASFORMATORE, DISTRIBUTORE).
     *
     * @param request DTO contenente i dati dell'articolo da vendere (ID prodotto,
     *                prezzo, quantità, ecc.).
     * @return ResponseEntity contenente i dettagli dell'articolo creato e lo stato
     *         HTTP 200 OK.
     */
    @PostMapping("/items")
    // Autorizza solo ruoli specifici che possono vendere merce
    @PreAuthorize("hasAnyAuthority('ROLE_PRODUTTORE', 'ROLE_TRASFORMATORE', 'ROLE_DISTRIBUTORE')")
    public ResponseEntity<MarketplaceItemResponse> creaMarketplaceItem(@RequestBody MarketplaceItemRequest request) {
        // Delega al service la logica di creazione e validazione dell'item
        MarketplaceItemResponse response = marketplaceService.creaItem(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pubblico per visualizzare tutti gli articoli attualmente in vendita
     * (catalogo).
     * Il catalogo mostrerà solo gli articoli il cui prodotto associato è stato
     * approvato.
     *
     * @return ResponseEntity contenente la lista degli articoli in vendita e lo
     *         stato HTTP 200 OK.
     */
    @GetMapping("/catalogo")
    public ResponseEntity<List<MarketplaceItemResponse>> getCatalogoPubblico() {
        // Recupera la lista filtrata dal service
        return ResponseEntity.ok(marketplaceService.getCatalogo());
    }
}
