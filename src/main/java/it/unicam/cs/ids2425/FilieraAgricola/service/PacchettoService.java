package it.unicam.cs.ids2425.FilieraAgricola.service;

import it.unicam.cs.ids2425.FilieraAgricola.dto.request.PacchettoRequest;
import it.unicam.cs.ids2425.FilieraAgricola.dto.response.PacchettoResponse;
import it.unicam.cs.ids2425.FilieraAgricola.model.*;
import it.unicam.cs.ids2425.FilieraAgricola.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service che gestisce il ciclo di vita dei Pacchetti (bundle).
 * <p>
 * <strong>Cosa fa:</strong>
 * Fornisce metodi transazionali per creare, aggiornare, eliminare, recuperare e
 * approvare pacchetti di prodotti.
 * <p>
 * <strong>Workflow e Logica:</strong>
 * <ul>
 * <li><strong>Creazione:</strong> Un distributore può creare un pacchetto
 * raggruppando più item.
 * Il pacchetto viene creato in stato di "bozza" o "in revisione" (tramite
 * {@link ContentSubmission})
 * e richiede l'approvazione di un Curatore per essere visibile nel catalogo
 * pubblico.</li>
 * <li><strong>Validazione:</strong> Si verifica che gli item inseriti nel
 * pacchetto siano già stati approvati.
 * Non è possibile vendere item non approvati all'interno di un pacchetto.</li>
 * <li><strong>Aggiornamento:</strong> Se un pacchetto approvato viene
 * modificato, il suo stato torna a
 * essere "non approvato" (o in revisione) per richiedere una nuova validazione
 * delle modifiche.</li>
 * <li><strong>Sicurezza:</strong> Viene applicato un controllo di ownership
 * tramite {@code checkOwnershipOrAdmin}
 * per garantire che solo il proprietario (o un admin) possa modificare i propri
 * pacchetti.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class PacchettoService {

    private final PacchettoRepository pacchettoRepository;
    private final UtenteRepository utenteRepository;
    private final MarketplaceItemRepository marketplaceItemRepository;
    private final MarketplaceItemPacchettoRepository marketplaceItemPacchettoRepository;
    private final ContentSubmissionRepository submissionRepository;

    // Servizio Curation iniettato
    private final CurationService curationService;

    /**
     * Crea un nuovo pacchetto e lo sottomette per l'approvazione.
     * <p>
     * <strong>Cosa succede:</strong>
     * <ol>
     * <li>Recupera il distributore e verifica i permessi.</li>
     * <li>Crea l'entità {@code Pacchetto} con i dati di base (nome, descrizione,
     * prezzo).</li>
     * <li>Genera una {@link ContentSubmission} associata per gestire il workflow di
     * approvazione.</li>
     * <li>Itera sugli ID degli item forniti, verificando che ciascuno sia
     * approvato,
     * e crea le associazioni {@link MarketplaceItemPacchetto}.</li>
     * </ol>
     *
     * @param request DTO con i dati del nuovo pacchetto.
     * @return DTO del pacchetto creato.
     * @throws RuntimeException      Se il distributore o un item non esistono.
     * @throws IllegalStateException Se si tenta di inserire un item non approvato.
     */
    @Transactional
    public PacchettoResponse creaPacchetto(PacchettoRequest request) {
        Utente distributore = utenteRepository.findById(request.getDistributoreId())
                .orElseThrow(() -> new RuntimeException("Distributore non trovato"));

        checkOwnershipOrAdmin(distributore.getId(), "creare pacchetti");

        Pacchetto pacchetto = new Pacchetto();
        pacchetto.setNome(request.getNome());
        pacchetto.setDescrizione(request.getDescrizione());
        pacchetto.setDistributore(distributore);
        pacchetto.setPrezzo_totale(request.getPrezzoTotale());

        Pacchetto savedPacchetto = pacchettoRepository.save(pacchetto);

        ContentSubmission submission = new ContentSubmission(savedPacchetto.getId(), "PACCHETTO");
        ContentSubmission savedSubmission = submissionRepository.save(submission);

        savedPacchetto.setSubmission(savedSubmission); // Campo 'submission' aggiunto al modello
        pacchettoRepository.save(savedPacchetto);

        for (Long itemId : request.getMarketplaceItemIds()) {
            MarketplaceItem item = marketplaceItemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("MarketplaceItem non trovato con id: " + itemId));

            if (item.getProdotto().getSubmission() == null ||
                    item.getProdotto().getSubmission().getStatus() != StatoContenuto.APPROVATO) {
                throw new IllegalStateException(
                        "Impossibile aggiungere al pacchetto l'item non approvato: " + item.getId());
            }

            MarketplaceItemPacchetto link = new MarketplaceItemPacchetto();
            link.setPacchetto(savedPacchetto);
            link.setItem(item);
            link.setQuantita(1);

            marketplaceItemPacchettoRepository.save(link);
            savedPacchetto.getItems().add(link);
        }

        return new PacchettoResponse(savedPacchetto);
    }

    /**
     * Restituisce tutti i pacchetti approvati visibili nel marketplace.
     *
     * @return Lista di pacchetti approvati.
     */
    public List<PacchettoResponse> getAllPacchetti() {
        return pacchettoRepository.findAll()
                .stream()
                .filter(p -> p.getSubmission() != null &&
                        p.getSubmission().getStatus() == StatoContenuto.APPROVATO)
                .map(PacchettoResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Aggiorna un pacchetto esistente.
     * <p>
     * <strong>Nota importante:</strong>
     * Se il pacchetto era già approvato, la modifica ne invalida lo stato
     * riportandolo
     * in BOZZA (o IN_REVISIONE), richiedendo una nuova approvazione da parte di un
     * Curatore.
     *
     * @param id      ID del pacchetto da aggiornare.
     * @param request Nuovi dati del pacchetto.
     * @return Il pacchetto aggiornato.
     */
    @Transactional
    public PacchettoResponse aggiornaPacchetto(Long id, PacchettoRequest request) {
        Pacchetto pacchetto = pacchettoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pacchetto non trovato"));

        checkOwnershipOrAdmin(pacchetto.getDistributore().getId(), "aggiornare");

        pacchetto.setNome(request.getNome());
        pacchetto.setDescrizione(request.getDescrizione());
        pacchetto.setPrezzo_totale(request.getPrezzoTotale());

        pacchetto.getItems().clear();
        marketplaceItemPacchettoRepository.deleteAllByPacchettoId(id); // Metodo aggiunto al repository

        for (Long itemId : request.getMarketplaceItemIds()) {
            MarketplaceItem item = marketplaceItemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("MarketplaceItem non trovato con id: " + itemId));

            if (item.getProdotto().getSubmission() == null ||
                    item.getProdotto().getSubmission().getStatus() != StatoContenuto.APPROVATO) {
                throw new IllegalStateException(
                        "Impossibile aggiungere al pacchetto l'item non approvato: " + item.getId());
            }

            MarketplaceItemPacchetto link = new MarketplaceItemPacchetto();
            link.setPacchetto(pacchetto);
            link.setItem(item);
            link.setQuantita(1);

            pacchetto.getItems().add(link);
        }

        ContentSubmission submission = pacchetto.getSubmission();
        if (submission != null && submission.getStatus() != StatoContenuto.BOZZA) {
            submission.setStatus(StatoContenuto.BOZZA);
            submission.setFeedbackCuratore("Modificato, richiede nuova approvazione.");
            submission.updateState();
            submissionRepository.save(submission);
        }

        return new PacchettoResponse(pacchettoRepository.save(pacchetto));
    }

    /**
     * Elimina un pacchetto dal sistema.
     *
     * @param id ID del pacchetto.
     */
    @Transactional
    public void eliminaPacchetto(Long id) {
        Pacchetto pacchetto = pacchettoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pacchetto non trovato"));

        checkOwnershipOrAdmin(pacchetto.getDistributore().getId(), "eliminare");

        pacchettoRepository.deleteById(id);
    }

    /**
     * Recupera i pacchetti creati da un determinato distributore.
     *
     * @param distributoreId ID del distributore.
     * @return Lista dei suoi pacchetti.
     */
    public List<PacchettoResponse> getByDistributore(Long distributoreId) {
        checkOwnershipOrAdmin(distributoreId, "visualizzare i pacchetti");

        return pacchettoRepository.findByDistributoreId(distributoreId)
                .stream()
                .map(PacchettoResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Recupera la lista dei pacchetti in attesa di approvazione per i Curatori.
     *
     * @return Lista di pacchetti in stato IN_REVISIONE.
     */
    @Transactional(readOnly = true)
    public List<PacchettoResponse> getPacchettiDaApprovare() {
        List<Long> idsPacchetti = submissionRepository.findByStatus(StatoContenuto.IN_REVISIONE)
                .stream()
                .filter(s -> s.getSubmittableEntityType().equals("PACCHETTO"))
                .map(ContentSubmission::getSubmittableEntityId)
                .collect(Collectors.toList());

        return pacchettoRepository.findAllById(idsPacchetti)
                .stream()
                .map(PacchettoResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Approva un pacchetto, rendendolo pubblico.
     * Delega la logica al {@link CurationService}.
     *
     * @param id ID del pacchetto da approvare.
     */
    @Transactional
    public void approvaPacchetto(Long id) {
        Pacchetto pacchetto = pacchettoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pacchetto non trovato"));

        ContentSubmission submission = pacchetto.getSubmission();
        if (submission == null) {
            throw new RuntimeException("Pacchetto non sottomesso per l'approvazione");
        }
        curationService.approvaContenuto(submission.getId());
    }

    private void checkOwnershipOrAdmin(Long ownerId, String azione) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Nessun utente autenticato.");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_CURATORE") || role.equals("ROLE_GESTORE"));

        if (isAdmin) {
            return;
        }

        String userEmail = authentication.getName();
        Optional<Utente> utenteAttuale = utenteRepository.findByEmail(userEmail);

        if (utenteAttuale.isEmpty() || !utenteAttuale.get().getId().equals(ownerId)) {
            throw new AccessDeniedException("Non hai i permessi per " + azione + ".");
        }
    }
}