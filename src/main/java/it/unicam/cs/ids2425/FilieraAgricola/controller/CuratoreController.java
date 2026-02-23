package it.unicam.cs.ids2425.FilieraAgricola.controller;

import it.unicam.cs.ids2425.FilieraAgricola.model.ContentSubmission;
import it.unicam.cs.ids2425.FilieraAgricola.service.CurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller per la gestione delle revisioni dei contenuti da parte dei
 * curatori.
 */
@RestController
@RequestMapping("/api/curatore")
@RequiredArgsConstructor
public class CuratoreController {

    private final CurationService curationService;

    /**
     * Restituisce i contenuti in revisione.
     * 
     * @return lista di submissions in revisione.
     */
    @PreAuthorize("hasRole('CURATORE')")
    @GetMapping("/revisione")
    public ResponseEntity<List<ContentSubmission>> getContenutiInRevisione() {
        return ResponseEntity.ok(curationService.getContenutiInRevisione());
    }

    /**
     * Approva un contenuto.
     * 
     * @param id id del contenuto.
     * @return la submission approvata.
     */
    @PreAuthorize("hasRole('CURATORE')")
    @PostMapping("/approva/{id}")
    public ResponseEntity<ContentSubmission> approvaContenuto(@PathVariable Long id) {
        return ResponseEntity.ok(curationService.approvaContenuto(id));
    }

    /**
     * Rifiuta un contenuto con un feedback.
     * 
     * @param id   id del contenuto.
     * @param body body con il feedback.
     * @return la submission rifiutata.
     */
    @PreAuthorize("hasRole('CURATORE')")
    @PostMapping("/rifiuta/{id}")
    public ResponseEntity<ContentSubmission> rifiutaContenuto(@PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String feedback = body.get("feedback");
        return ResponseEntity.ok(curationService.rifiutaContenuto(id, feedback));
    }
}
