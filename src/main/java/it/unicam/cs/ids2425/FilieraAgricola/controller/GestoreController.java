package it.unicam.cs.ids2425.FilieraAgricola.controller;

import it.unicam.cs.ids2425.FilieraAgricola.dto.request.AccreditaDistributoreRequest;
import it.unicam.cs.ids2425.FilieraAgricola.dto.request.AccreditaProduttoreRequest;
import it.unicam.cs.ids2425.FilieraAgricola.dto.request.AccreditaRequest;
import it.unicam.cs.ids2425.FilieraAgricola.dto.request.AccreditaTrasformatoreRequest;
import it.unicam.cs.ids2425.FilieraAgricola.service.GestoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gestore/utenti")
@RequiredArgsConstructor
public class GestoreController {

    private final GestoreService gestoreService;

    @PreAuthorize("hasRole('GESTORE')")
    @PostMapping("/{id}/accredita-produttore")
    public ResponseEntity<Void> accreditaProduttore(@PathVariable Long id,
            @RequestBody AccreditaProduttoreRequest request) {
        gestoreService.accreditaProduttore(id, request);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('GESTORE')")
    @PostMapping("/{id}/accredita-trasformatore")
    public ResponseEntity<Void> accreditaTrasformatore(@PathVariable Long id,
            @RequestBody AccreditaTrasformatoreRequest request) {
        gestoreService.accreditaTrasformatore(id, request);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('GESTORE')")
    @PostMapping("/{id}/accredita-distributore")
    public ResponseEntity<Void> accreditaDistributore(@PathVariable Long id,
            @RequestBody AccreditaDistributoreRequest request) {
        gestoreService.accreditaDistributore(id, request);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('GESTORE')")
    @PostMapping("/{id}/accredita-ruolo")
    public ResponseEntity<Void> accreditaRuoloBase(@PathVariable Long id, @RequestBody AccreditaRequest request) {
        gestoreService.accreditaRuoloBase(id, request.getRuolo());
        return ResponseEntity.ok().build();
    }
}
