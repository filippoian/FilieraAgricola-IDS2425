package it.unicam.cs.ids2425.FilieraAgricola.config;

import it.unicam.cs.ids2425.FilieraAgricola.security.JwtUtils;
import it.unicam.cs.ids2425.FilieraAgricola.security.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * Filtro di sicurezza eseguito una volta per ogni richiesta HTTP.
 * <p>
 * Questo componente intercetta le richieste in arrivo per cercare un token JWT
 * nell'header "Authorization". Se viene trovato un token valido, il filtro
 * estrae l'identità dell'utente e configura il {@link SecurityContextHolder}
 * di Spring Security, autenticando l'utente per la richiesta corrente.
 * </p>
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Costruttore per l'iniezione delle dipendenze necessarie.
     *
     * @param jwtUtils           Componente per la validazione e il parsing del token JWT.
     * @param userDetailsService Servizio per il caricamento dei dettagli utente dal database.
     */
    public JwtAuthFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Esegue la logica di filtro interna.
     * <ol>
     * <li>Controlla se l'header "Authorization" contiene un token Bearer.</li>
     * <li>Estrae l'username (email) dal token.</li>
     * <li>Se l'utente non è già autenticato nel contesto, carica i dettagli utente.</li>
     * <li>Se il token è valido, crea un oggetto di autenticazione e lo imposta nel contesto di sicurezza.</li>
     * </ol>
     *
     * @param request     La richiesta HTTP in arrivo.
     * @param response    La risposta HTTP.
     * @param filterChain La catena di filtri da proseguire.
     * @throws ServletException In caso di errori servlet.
     * @throws IOException      In caso di errori di I/O.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtUtils.getEmailFromToken(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtils.validateToken(token)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}