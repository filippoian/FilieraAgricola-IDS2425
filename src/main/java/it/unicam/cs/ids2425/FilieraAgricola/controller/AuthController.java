package it.unicam.cs.ids2425.FilieraAgricola.controller;

import it.unicam.cs.ids2425.FilieraAgricola.dto.request.LoginRequest;
import it.unicam.cs.ids2425.FilieraAgricola.dto.request.RegistrazioneRequest;
import it.unicam.cs.ids2425.FilieraAgricola.dto.response.LoginResponse;
import it.unicam.cs.ids2425.FilieraAgricola.dto.response.MessageResponse;
import it.unicam.cs.ids2425.FilieraAgricola.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST responsabile della gestione dei punti di accesso per
 * l'autenticazione
 * e la registrazione degli utenti.
 * <p>
 * Espone le API per registrare nuovi utenti e per effettuare il login,
 * restituendo
 * i token JWT necessari per le richieste successive.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Costruttore per l'iniezione del servizio di autenticazione.
     *
     * @param authService Il servizio di business per le operazioni di auth.
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Gestisce la richiesta di registrazione di un nuovo utente.
     *
     * @param request Il corpo della richiesta contenente i dati dell'utente da
     *                registrare.
     * @return {@link ResponseEntity} con stato 200 OK e il token JWT se la
     *         registrazione ha successo.
     *         In caso di errore, restituisce 400 Bad Request con un messaggio
     *         descrittivo.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registra(@RequestBody RegistrazioneRequest request) {
        try {
            return ResponseEntity.ok(authService.registraUtente(request));
        } catch (Exception e) {
            // Log dell'errore per il debug e ritorno di un messaggio leggibile
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Errore durante la registrazione: " + e.getMessage()));
        }
    }

    /**
     * Gestisce la richiesta di login di un utente esistente.
     *
     * @param request Il corpo della richiesta contenente le credenziali (email e
     *                password).
     * @return {@link ResponseEntity} con:
     *         <ul>
     *         <li>200 OK e il token JWT se l'autenticazione ha successo.</li>
     *         <li>403 Forbidden se l'account esiste ma non è ancora abilitato (es.
     *         in attesa di approvazione).</li>
     *         <li>401 Unauthorized se le credenziali sono errate.</li>
     *         </ul>
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Se il login ha successo, ritorna il token
            LoginResponse loginResponse = authService.login(request);
            return ResponseEntity.ok(loginResponse);

        } catch (DisabledException e) {
            // Se l'utente è 'enabled=false'
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Account in attesa di approvazione da parte di un gestore."));

        } catch (AuthenticationException e) {
            // Per qualsiasi altro errore di autenticazione (es. password errata)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Email o password errati."));
        }
    }
}
