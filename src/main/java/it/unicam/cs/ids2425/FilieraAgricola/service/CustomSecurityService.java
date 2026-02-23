package it.unicam.cs.ids2425.FilieraAgricola.service;

import it.unicam.cs.ids2425.FilieraAgricola.security.UserDetailsImpl; // <-- IMPORT CORRETTO
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Servizio di sicurezza personalizzato per l'autorizzazione degli utenti.
 */
@Service("customSecurityService")
public class CustomSecurityService {

    /**
     * Verifica se l'utente autenticato corrisponde all'ID utente specificato.
     * 
     * @param authentication L'oggetto Authentication contenente il contesto di
     *                       sicurezza.
     * @param userId         L'ID dell'utente da verificare contro il principal.
     * @return true se l'utente autenticato è autorizzato per l'ID in esame, false
     *         altrimenti.
     */
    public boolean hasUserId(Authentication authentication, Long userId) {
        System.out.println("--- INIZIO CONTROLLO SICUREZZA PERSONALIZZATO ---");

        if (authentication == null) {
            System.out.println("ERRORE: Oggetto Authentication è nullo.");
            return false;
        }
        if (userId == null) {
            System.out.println("ERRORE: ID utente dall'URL è nullo.");
            return false;
        }

        Object principal = authentication.getPrincipal();
        System.out.println("ID richiesto dall'URL (#utenteId): " + userId);
        System.out.println("Tipo di 'principal' nel contesto di sicurezza: " + principal.getClass().getName());

        // --- INIZIO CORREZIONE ---
        // Controlliamo l'istanza della classe corretta
        if (principal instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) principal;
            Long authenticatedUserId = userDetails.getId();
            // --- FINE CORREZIONE ---

            System.out.println("ID utente autenticato dal token: " + authenticatedUserId);

            boolean isMatch = authenticatedUserId.equals(userId);
            System.out.println("Confronto ID: " + authenticatedUserId + " == " + userId + " ? --> " + isMatch);
            System.out.println("--- FINE CONTROLLO SICUREZZA ---");
            return isMatch;
        } else {
            // --- INIZIO CORREZIONE ---
            System.out
                    .println("ERRORE: Il 'principal' non è un'istanza di UserDetailsImpl. Impossibile ottenere l'ID.");
            // --- FINE CORREZIONE ---
            System.out.println("--- FINE CONTROLLO SICUREZZA ---");
            return false;
        }
    }
}