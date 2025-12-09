package it.unicam.cs.ids2425.FilieraAgricola.service;

import it.unicam.cs.ids2425.FilieraAgricola.dto.request.LoginRequest;
import it.unicam.cs.ids2425.FilieraAgricola.dto.request.RegistrazioneRequest;
import it.unicam.cs.ids2425.FilieraAgricola.dto.response.LoginResponse;
import it.unicam.cs.ids2425.FilieraAgricola.exception.EmailAlreadyExistException;
import it.unicam.cs.ids2425.FilieraAgricola.model.ERole;
import it.unicam.cs.ids2425.FilieraAgricola.model.Role;
import it.unicam.cs.ids2425.FilieraAgricola.model.UserProfile;
import it.unicam.cs.ids2425.FilieraAgricola.model.Utente;
import it.unicam.cs.ids2425.FilieraAgricola.repository.RoleRepository;
import it.unicam.cs.ids2425.FilieraAgricola.repository.UserProfileRepository;
import it.unicam.cs.ids2425.FilieraAgricola.repository.UtenteRepository;
import it.unicam.cs.ids2425.FilieraAgricola.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service responsabile della gestione dell'autenticazione e della registrazione
 * degli utenti.
 * <p>
 * Fornisce metodi per registrare nuovi utenti nel sistema, gestendo la
 * creazione
 * delle credenziali, dei profili associati e l'assegnazione dei ruoli, nonché
 * il processo di login e generazione del token JWT.
 */
@Service
public class AuthService {

    private final UtenteRepository utenteRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserProfileRepository userProfileRepository;

    /**
     * Costruttore per l'iniezione delle dipendenze.
     *
     * @param utenteRepository      Repository per l'accesso ai dati degli utenti.
     * @param roleRepository        Repository per l'accesso ai dati dei ruoli.
     * @param passwordEncoder       Componente per la cifratura delle password.
     * @param jwtUtils              Utility per la generazione e validazione dei
     *                              token JWT.
     * @param authenticationManager Manager di autenticazione di Spring Security.
     * @param userProfileRepository Repository per l'accesso ai dati dei profili
     *                              utente.
     */
    @Autowired
    public AuthService(UtenteRepository utenteRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils,
                       AuthenticationManager authenticationManager,
                       UserProfileRepository userProfileRepository) {
        this.utenteRepository = utenteRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.userProfileRepository = userProfileRepository;
    }

    /**
     * Gestisce la registrazione di un nuovo utente nel sistema.
     * <p>
     * Questo metodo esegue i seguenti passaggi:
     * <ol>
     * <li>Verifica che l'email non sia già presente nel sistema.</li>
     * <li>Crea un nuovo account utente con le credenziali fornite.</li>
     * <li>Assegna il ruolo richiesto. Se il ruolo richiede approvazione (non è
     * UTENTE GENERICO o ACQUIRENTE),
     * l'utente viene creato in stato disabilitato.</li>
     * <li>Salva l'utente e crea il profilo utente associato.</li>
     * <li>Genera un token JWT per la sessione (anche se l'utente potrebbe non
     * essere abilitato all'uso immediato).</li>
     * </ol>
     *
     * @param request DTO contenente i dati per la registrazione.
     * @return Un oggetto {@link LoginResponse} contenente il token JWT e i dettagli
     *         dell'utente registrato.
     * @throws EmailAlreadyExistException Se l'email fornita è già associata a un
     *                                    account esistente.
     * @throws IllegalArgumentException   Se il ruolo non è specificato nella
     *                                    richiesta.
     * @throws RuntimeException           Se il ruolo specificato non viene trovato
     *                                    nel database.
     */
    @Transactional
    public LoginResponse registraUtente(RegistrazioneRequest request) {

        if (utenteRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistException("Email già registrata");
        }

        if (request.getRuolo() == null) {
            throw new IllegalArgumentException("Il ruolo è obbligatorio per la registrazione.");
        }

        // Crea l'entità UserAccount (Utente)
        Utente nuovoUtente = new Utente();
        nuovoUtente.setEmail(request.getEmail());
        nuovoUtente.setPassword(passwordEncoder.encode(request.getPassword()));

        Set<Role> roles = new HashSet<>();
        // Trova il ruolo richiesto
        Role userRole = roleRepository.findByName(request.getRuolo())
                .orElseThrow(() -> new RuntimeException("Errore: Ruolo " + request.getRuolo() + " non trovato."));
        roles.add(userRole);

        // Se il ruolo è "semplice", abilita l'utente immediatamente
        if (request.getRuolo() == ERole.ROLE_UTENTEGENERICO || request.getRuolo() == ERole.ROLE_ACQUIRENTE) {
            nuovoUtente.setEnabled(true);
        } else {
            // Altrimenti, l'utente è creato ma 'enabled' resta 'false' (default)
            // e richiederà l'approvazione del Gestore.
            nuovoUtente.setEnabled(false);
        }

        nuovoUtente.setRoles(roles);

        // Salva l'utente per generare l'ID
        Utente salvato = utenteRepository.save(nuovoUtente);

        // Crea l'entità UserProfile collegata
        UserProfile nuovoProfilo = new UserProfile(
                salvato,
                request.getNome(),
                request.getCognome(),
                request.getTelefono());
        userProfileRepository.save(nuovoProfilo);

        salvato.setUserProfile(nuovoProfilo);

        // Genera il token
        List<String> ruoliPerToken = salvato.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());

        String token = jwtUtils.generateToken(salvato.getEmail(), ruoliPerToken);

        return new LoginResponse(token, salvato);
    }

    /**
     * Autentica un utente nel sistema utilizzando email e password.
     * <p>
     * Se le credenziali sono corrette e l'utente è abilitato, viene generato e
     * restituito un token JWT.
     *
     * @param request DTO contenente le credenziali di login.
     * @return Un oggetto {@link LoginResponse} contenente il token JWT e i dettagli
     *         dell'utente autenticato.
     * @throws org.springframework.security.authentication.DisabledException Se
     *                                                                       l'utente
     *                                                                       è
     *                                                                       disabilitato
     *                                                                       (es. in
     *                                                                       attesa
     *                                                                       di
     *                                                                       approvazione).
     * @throws org.springframework.security.core.AuthenticationException     Se le
     *                                                                       credenziali
     *                                                                       non
     *                                                                       sono
     *                                                                       valide.
     * @throws RuntimeException                                              Se si
     *                                                                       verifica
     *                                                                       un
     *                                                                       errore
     *                                                                       interno
     *                                                                       nel
     *                                                                       recupero
     *                                                                       dell'utente
     *                                                                       dopo
     *                                                                       l'autenticazione.
     */
    public LoginResponse login(LoginRequest request) {
        // Authenticate lancia DisabledException se utente.isEnabled() è false
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Utente utente = utenteRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Errore interno: Utente non trovato dopo autenticazione"));

        List<String> ruoliPerToken = utente.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());

        String token = jwtUtils.generateToken(utente.getEmail(), ruoliPerToken);

        return new LoginResponse(token, utente);
    }
}