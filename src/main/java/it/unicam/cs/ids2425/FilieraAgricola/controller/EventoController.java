package it.unicam.cs.ids2425.FilieraAgricola.controller;

import it.unicam.cs.ids2425.FilieraAgricola.dto.request.EventoRequest;
import it.unicam.cs.ids2425.FilieraAgricola.dto.response.EventoResponse;
import it.unicam.cs.ids2425.FilieraAgricola.model.ContentSubmission;
import it.unicam.cs.ids2425.FilieraAgricola.service.CurationService;
import it.unicam.cs.ids2425.FilieraAgricola.service.EventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller per la gestione degli eventi.
 */
@RestController
@RequestMapping("/api/eventi")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;
    private final CurationService curationService;

    /**
     * Crea un nuovo evento.
     * 
     * @param request Dati per la creazione dell'evento.
     * @return L'evento creato in formato di risposta.
     */
    @PreAuthorize("hasRole('ANIMATORE')")
    @PostMapping
    public ResponseEntity<EventoResponse> creaEvento(@RequestBody EventoRequest request) {
        return ResponseEntity.ok(eventoService.creaEvento(request));
    }

    /**
     * Sottomette un evento per la revisione da parte di un curatore.
     * 
     * @param id L'identificativo dell'evento da sottomettere.
     * @return I dettagli della sottomissione effettuata.
     */
    @PreAuthorize("hasRole('ANIMATORE')")
    @PostMapping("/{id}/sottometti")
    public ResponseEntity<ContentSubmission> sottomettiEvento(@PathVariable Long id) {
        ContentSubmission submission = curationService.findSubmissionByEntity(id, "EVENTO");
        return ResponseEntity.ok(curationService.sottomettiContenuto(submission.getId()));
    }

    /**
     * Approva un evento precedentemente sottomesso.
     * 
     * @param id L'identificativo dell'evento da approvare.
     * @return Una risposta vuota di conferma (200 OK).
     */
    @PreAuthorize("hasRole('CURATORE')")
    @PostMapping("/{id}/approva")
    public ResponseEntity<Void> approvaEvento(@PathVariable Long id) {
        eventoService.approvaEvento(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Restituisce la lista di tutti gli eventi che sono stati approvati.
     * 
     * @return Una lista di tutti gli eventi validati.
     */
    @GetMapping("/approvati")
    public ResponseEntity<List<EventoResponse>> getEventiApprovati() {
        return ResponseEntity.ok(eventoService.getEventiApprovati());
    }
}
