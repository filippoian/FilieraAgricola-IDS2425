package it.unicam.cs.ids2425.FilieraAgricola.config;

import it.unicam.cs.ids2425.FilieraAgricola.security.AuthEntryPointJwt;
import it.unicam.cs.ids2425.FilieraAgricola.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configurazione principale della sicurezza.
 * Definisce le regole di accesso HTTP, la gestione delle sessioni (stateless) e integra il filtro JWT.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    /**
     * Configura il provider collegando il servizio utenti (DB) e l'algoritmo di codifica password.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Espone l'AuthenticationManager di Spring necessario per processare i login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Definisce l'algoritmo BCrypt per l'hashing sicuro delle password.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Definisce la catena di filtri di sicurezza: disabilita CSRF, imposta sessioni stateless,
     * configura le whitelist degli URL e aggiunge il filtro JWT.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // PAGINE STATICHE E RISORSE PUBBLICHE ---
                        .requestMatchers("/mappa.html").permitAll() // Sblocca la pagina HTML
                        .requestMatchers("/index.html", "/").permitAll()
                        .requestMatchers("/favicon.ico").permitAll() // Sblocca l'icona del sito
                        .requestMatchers("/error").permitAll()       // Sblocca la pagina di errore

                        // API DI AUTENTICAZIONE ---
                        .requestMatchers("/api/auth/**").permitAll()

                        // API DI LETTURA (GET) ---
                        .requestMatchers(HttpMethod.GET, "/api/prodotti/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/aziende/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/marketplace/catalogo").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/eventi/approvati").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/mappa/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tracciabilita/**").permitAll()

                        // TUTTO IL RESTO RICHIEDE LOGIN ---
                        .anyRequest().authenticated()
                )

                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}