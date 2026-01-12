package it.unicam.cs.ids2425.FilieraAgricola.controller;

import it.unicam.cs.ids2425.FilieraAgricola.dto.request.PacchettoRequest;
import it.unicam.cs.ids2425.FilieraAgricola.dto.response.PacchettoResponse;
import it.unicam.cs.ids2425.FilieraAgricola.service.PacchettoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST per la gestione dei Pacchetti (bundle di prodotti).
 * <p>
 * Espone endpoint per:
 * <ul>
 * <li>Creazione e gestione (modifica/eliminazione) dei pacchetti da parte dei
 * Distributori.</li>
 * <li>Consultazione del catalogo pacchetti (pubblico/autenticato).</li>
 * <li>Flusso di approvazione dei pacchetti riservato ai Curatori.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/pacchetti")
@RequiredArgsConstructor
public class PacchettoController {

    private final PacchettoService pacchettoService;

    /**
     * Crea un nuovo pacchetto.
     * <p>
     * Accessibile solo dagli utenti con ruolo {@code DISTRIBUTORE}.
     * Il pacchetto nasce in stato "da approvare".
     *
     * @param request Dati del pacchetto da creare.
     * @return Il pacchetto creato.
     */
    @PreAuthorize("hasRole('DISTRIBUTORE')")
    @PostMapping
    public ResponseEntity<PacchettoResponse> creaPacchetto(@RequestBody PacchettoRequest request) {
        return ResponseEntity.ok(pacchettoService.creaPacchetto(request));
    }

    /**
     * Restituisce la lista di tutti i pacchetti <strong>approvati</strong>.
     * <p>
     * Accessibile a qualsiasi utente autenticato.
     *
     * @return Lista di pacchetti visibili.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<PacchettoResponse>> getAllPacchetti() {
        return ResponseEntity.ok(pacchettoService.getAllPacchetti());
    }

    /**
     * Recupera i pacchetti creati da uno specifico distributore.
     * <p>
     * Permesso a:
     * <ul>
     * <li>Gestori (admin).</li>
     * <li>L'utente stesso (se l'ID corrisponde a quello nei token).</li>
     * </ul>
     *
     * @param id ID del distributore.
     * @return Lista dei suoi pacchetti.
     */
    @PreAuthorize("hasRole('GESTORE') or @customSecurityService.hasUserId(authentication, #id)")
    @GetMapping("/distributore/{id}")
    public List<PacchettoResponse> getByDistributore(@PathVariable Long id) {
        return pacchettoService.getByDistributore(id);
    }

    /**
     * Restituisce la lista dei pacchetti in attesa di approvazione.
     * <p>
     * Accessibile solo ai Curatori.
     *
     * @return Pacchetti in stato {@code IN_REVISIONE}.
     */
    @PreAuthorize("hasRole('CURATORE')")
    @GetMapping("/da-approvare")
    public List<PacchettoResponse> pacchettiDaApprovare() {
        return pacchettoService.getPacchettiDaApprovare();
    }

    /**
     * Approva un pacchetto, rendendolo pubblico.
     * <p>
     * Accessibile solo ai Curatori.
     *
     * @param id ID del pacchetto da approvare.
     */
    @PreAuthorize("hasRole('CURATORE')")
    @PostMapping("/{id}/approva")
    public void approvaPacchetto(@PathVariable Long id) {
        pacchettoService.approvaPacchetto(id);
    }

    /**
     * Aggiorna un pacchetto esistente.
     * <p>
     * Accessibile solo ai Distributori (verrà controllata la proprietà nel
     * service).
     * Modificare un pacchetto ne richiede una nuova approvazione.
     *
     * @param id      ID del pacchetto.
     * @param request Nuovi dati.
     * @return Il pacchetto aggiornato.
     */
    @PreAuthorize("hasRole('DISTRIBUTORE')")
    @PutMapping("/{id}")
    public PacchettoResponse aggiornaPacchetto(@PathVariable Long id, @RequestBody PacchettoRequest request) {
        return pacchettoService.aggiornaPacchetto(id, request);
    }

    /**
     * Elimina un pacchetto.
     * <p>
     * Accessibile solo ai Distributori (proprietari).
     *
     * @param id ID del pacchetto da eliminare.
     * @return Conferma eliminazione.
     */
    @PreAuthorize("hasRole('DISTRIBUTORE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminaPacchetto(@PathVariable Long id) {
        pacchettoService.eliminaPacchetto(id);
        return ResponseEntity.ok("Pacchetto eliminato");
    }
}