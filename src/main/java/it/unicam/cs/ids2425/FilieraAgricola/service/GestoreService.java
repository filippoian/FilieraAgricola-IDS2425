package it.unicam.cs.ids2425.FilieraAgricola.service;

import it.unicam.cs.ids2425.FilieraAgricola.dto.request.AccreditaDistributoreRequest;
import it.unicam.cs.ids2425.FilieraAgricola.dto.request.AccreditaProduttoreRequest;
import it.unicam.cs.ids2425.FilieraAgricola.dto.request.AccreditaRequest;
import it.unicam.cs.ids2425.FilieraAgricola.dto.request.AccreditaTrasformatoreRequest;
import it.unicam.cs.ids2425.FilieraAgricola.model.*;
import it.unicam.cs.ids2425.FilieraAgricola.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service che gestisce le operazioni riservate al Gestore della piattaforma.
 * Si occupa principalmente dell'accreditamento degli utenti (Produttori,
 * Trasformatori, Distributori)
 * e dell'abilitazione dei ruoli base (Curatori, Animatori, ecc.).
 */
@Service
public class GestoreService {

    @Autowired
    UtenteRepository utenteRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserProfileRepository userProfileRepository;
    @Autowired
    FilieraPointRepository filieraPointRepository;
    @Autowired
    ContentSubmissionRepository submissionRepository;

    @Autowired
    ActorProfileProduttoreRepository actorProfileProduttoreRepository;
    @Autowired
    ActorProfileTrasformatoreRepository actorProfileTrasformatoreRepository;
    @Autowired
    ActorProfileDistributoreRepository actorProfileDistributoreRepository;

    /**
     * Abilita un utente che possiede un ruolo "base" (es. CURATORE, ANIMATORE,
     * GESTORE).
     * Questo metodo verifica che l'utente abbia il ruolo richiesto e lo imposta
     * come abilitato.
     *
     * @param userId    L'identificativo univoco dell'utente da accreditare.
     * @param ruoloNome Il ruolo che si intende abilitare (non deve essere un ruolo
     *                  di filiera come PRODUTTORE, ecc.).
     * @throws IllegalArgumentException se il ruolo specificato richiede un flusso
     *                                  di accredito specifico.
     * @throws RuntimeException         se l'utente o il ruolo non vengono trovati.
     * @throws IllegalStateException    se l'utente non possiede il ruolo
     *                                  specificato.
     */
    @Transactional
    public void accreditaRuoloBase(Long userId, ERole ruoloNome) {
        // Verifica che il ruolo non sia uno di quelli che richiedono procedure specifiche
        if (ruoloNome == ERole.ROLE_PRODUTTORE ||
                ruoloNome == ERole.ROLE_TRASFORMATORE ||
                ruoloNome == ERole.ROLE_DISTRIBUTORE) {
            throw new IllegalArgumentException("Usare l'endpoint di accredito specifico per questo ruolo.");
        }

        Utente utente = utenteRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Errore: Utente non trovato con id " + userId));

        // 1. Controlla che l'utente abbia già quel ruolo (assegnato in fase di
        // registrazione)
        Role ruolo = roleRepository.findByName(ruoloNome)
                .orElseThrow(() -> new RuntimeException(
                        "Errore: Ruolo " + ruoloNome + " non trovato."));

        if (!utente.getRoles().contains(ruolo)) {
            throw new IllegalStateException("L'utente non si è registrato con il ruolo " + ruoloNome);
        }

        // 2. Abilita l'utente rendendolo attivo nel sistema
        utente.setEnabled(true);

        utenteRepository.save(utente);
    }

    /**
     * Esegue l'accredito completo di un PRODUTTORE.
     * Questa operazione comprende:
     * <ol>
     * <li>Recupero dell'utente e del suo profilo base.</li>
     * <li>Creazione e approvazione automatica del FilieraPoint associato (di tipo
     * AZIENDA_AGRICOLA).</li>
     * <li>Creazione del profilo specifico (ActorProfile_Produttore) con i dati
     * aziendali forniti.</li>
     * <li>Abilitazione dell'utente nel sistema.</li>
     * </ol>
     *
     * @param userId  L'identificativo dell'utente.
     * @param request Oggetto contenente i dati per l'accreditamento (dati azienda,
     *                punto filiera, ecc.).
     * @throws RuntimeException se l'utente o il profilo base non vengono trovati.
     */
    @Transactional
    public void accreditaProduttore(Long userId, AccreditaProduttoreRequest request) {
        // 1. Recupera Utente e UserProfile
        Utente utente = utenteRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con id: " + userId));

        UserProfile userProfile = userProfileRepository.findByUtenteId(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Profilo utente non trovato per id: " + userId));

        // 2. Crea il FilieraPoint (Azienda Agricola) e lo approva
        FilieraPoint point = creaEApprovaFilieraPoint(
                request.getNomePunto(),
                request.getDescrizionePunto(),
                request.getIndirizzoPunto(),
                request.getLatitudine(),
                request.getLongitudine(),
                TipoFilieraPoint.AZIENDA_AGRICOLA,
                utente);

        // 3. Crea l'ActorProfile_Produttore collegandolo al profilo e al punto creato
        ActorProfile_Produttore profiloProduttore = new ActorProfile_Produttore();
        profiloProduttore.setUserProfile(userProfile);
        profiloProduttore.setFilieraPoint(point);
        profiloProduttore.setRagioneSociale(request.getRagioneSociale());
        profiloProduttore.setPartitaIva(request.getPartitaIva());
        profiloProduttore.setDescrizioneAzienda(request.getDescrizioneAzienda());
        actorProfileProduttoreRepository.save(profiloProduttore);

        // 4. Abilita l'utente (il ruolo è già presente dalla registrazione)
        utente.setEnabled(true);
        utenteRepository.save(utente);
    }

    /**
     * Esegue l'accredito completo di un TRASFORMATORE.
     * Simile all'accredito produttore, ma crea un FilieraPoint di tipo
     * LABORATORIO_TRASFORMAZIONE
     * e un profilo ActorProfile_Trasformatore.
     *
     * @param userId  L'identificativo dell'utente.
     * @param request Dati per l'accreditamento del trasformatore.
     * @throws RuntimeException se l'utente o il profilo vengono meno.
     */
    @Transactional
    public void accreditaTrasformatore(Long userId, AccreditaTrasformatoreRequest request) {
        Utente utente = utenteRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con id: " + userId));

        UserProfile userProfile = userProfileRepository.findByUtenteId(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Profilo utente non trovato per id: " + userId));

        // Crea Lab. Trasformazione
        FilieraPoint point = creaEApprovaFilieraPoint(
                request.getNomePunto(),
                request.getDescrizionePunto(),
                request.getIndirizzoPunto(),
                request.getLatitudine(),
                request.getLongitudine(),
                TipoFilieraPoint.LABORATORIO_TRASFORMAZIONE,
                utente);

        // Crea profilo Trasformatore
        ActorProfile_Trasformatore profiloTrasformatore = new ActorProfile_Trasformatore();
        profiloTrasformatore.setUserProfile(userProfile);
        profiloTrasformatore.setFilieraPoint(point);
        profiloTrasformatore.setRagioneSociale(request.getRagioneSociale());
        profiloTrasformatore.setPartitaIva(request.getPartitaIva());
        profiloTrasformatore.setDescrizioneLaboratorio(request.getDescrizioneLaboratorio());
        actorProfileTrasformatoreRepository.save(profiloTrasformatore);

        utente.setEnabled(true);
        utenteRepository.save(utente);
    }

    /**
     * Esegue l'accredito completo di un DISTRIBUTORE.
     * Crea un FilieraPoint di tipo PUNTO_VENDITA e un profilo
     * ActorProfile_Distributore.
     *
     * @param userId  L'identificativo dell'utente.
     * @param request Dati per l'accreditamento del distributore (inclusa
     *                logistica).
     * @throws RuntimeException se l'utente o il profilo mancano.
     */
    @Transactional
    public void accreditaDistributore(Long userId, AccreditaDistributoreRequest request) {
        Utente utente = utenteRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con id: " + userId));

        UserProfile userProfile = userProfileRepository.findByUtenteId(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Profilo utente non trovato per id: " + userId));

        // Crea Punto Vendita
        FilieraPoint point = creaEApprovaFilieraPoint(
                request.getNomePunto(),
                request.getDescrizionePunto(),
                request.getIndirizzoPunto(),
                request.getLatitudine(),
                request.getLongitudine(),
                TipoFilieraPoint.PUNTO_VENDITA,
                utente);

        // Crea profilo Distributore
        ActorProfile_Distributore profiloDistributore = new ActorProfile_Distributore();
        profiloDistributore.setUserProfile(userProfile);
        profiloDistributore.setFilieraPoint(point);
        profiloDistributore.setRagioneSociale(request.getRagioneSociale());
        profiloDistributore.setPartitaIva(request.getPartitaIva());
        profiloDistributore.setInfoLogistica(request.getInfoLogistica());
        actorProfileDistributoreRepository.save(profiloDistributore);

        utente.setEnabled(true);
        utenteRepository.save(utente);
    }

    // --- Metodi Helper ---

    /**
     * Metodo helper per creare un FilieraPoint e approvarlo automaticamente.
     * Viene utilizzato durante le procedure di accreditamento per generare il punto
     * fisico associato all'attore.
     * Crea inoltre un oggetto {@link ContentSubmission} associato al punto,
     * impostandolo direttamente allo stato APPROVATO.
     *
     * @param nome      Nome del punto della filiera.
     * @param desc      Descrizione dettagliata del punto.
     * @param indirizzo Indirizzo fisico.
     * @param lat       Latitudine geografica.
     * @param lon       Longitudine geografica.
     * @param tipo      Tipo enumerativo di FilieraPoint (es. AZIENDA_AGRICOLA).
     * @param utente    L'utente proprietario del punto.
     * @return L'oggetto {@link FilieraPoint} salvato nel database e approvato.
     */
    private FilieraPoint creaEApprovaFilieraPoint(String nome, String desc, String indirizzo,
                                                  java.math.BigDecimal lat, java.math.BigDecimal lon,
                                                  TipoFilieraPoint tipo, Utente utente) {

        FilieraPoint point = new FilieraPoint();
        point.setNomePunto(nome);
        point.setDescrizione(desc);
        point.setIndirizzo(indirizzo);
        point.setLatitudine(lat);
        point.setLongitudine(lon);
        point.setTipo(tipo);
        point.setUtente(utente);
        FilieraPoint savedPoint = filieraPointRepository.save(point);

        // Crea la submission automatica e la approva
        ContentSubmission submission = new ContentSubmission(savedPoint.getId(), "FILIERAPOINT");
        submission.setStatus(StatoContenuto.APPROVATO);
        submission.setFeedbackCuratore("Approvato automaticamente dal Gestore al momento dell'accredito.");
        submission.updateState();
        ContentSubmission savedSubmission = submissionRepository.save(submission);

        savedPoint.setSubmission(savedSubmission);
        return filieraPointRepository.save(savedPoint);
    }
}