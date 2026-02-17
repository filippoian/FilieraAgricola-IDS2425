package it.unicam.cs.ids2425.FilieraAgricola.controller;

import it.unicam.cs.ids2425.FilieraAgricola.model.ContentSubmission;
import it.unicam.cs.ids2425.FilieraAgricola.service.CurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/curatore")
@RequiredArgsConstructor
public class CuratoreController {

    private final CurationService curationService;

    @PreAuthorize("hasRole('CURATORE')")
    @GetMapping("/revisione")
    public ResponseEntity<List<ContentSubmission>> getContenutiInRevisione() {
        return ResponseEntity.ok(curationService.getContenutiInRevisione());
    }

    @PreAuthorize("hasRole('CURATORE')")
    @PostMapping("/approva/{id}")
    public ResponseEntity<ContentSubmission> approvaContenuto(@PathVariable Long id) {
        return ResponseEntity.ok(curationService.approvaContenuto(id));
    }

    @PreAuthorize("hasRole('CURATORE')")
    @PostMapping("/rifiuta/{id}")
    public ResponseEntity<ContentSubmission> rifiutaContenuto(@PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String feedback = body.get("feedback");
        return ResponseEntity.ok(curationService.rifiutaContenuto(id, feedback));
    }
}
