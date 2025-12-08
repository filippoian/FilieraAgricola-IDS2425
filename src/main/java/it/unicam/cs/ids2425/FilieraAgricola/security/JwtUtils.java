package it.unicam.cs.ids2425.FilieraAgricola.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

/**
 * Utility per la gestione del ciclo di vita dei token JWT (generazione, parsing e validazione).
 */
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${filiera.jwt.secret}")
    private String jwtSecret;

    @Value("${filiera.jwt.expirationMs}")
    private int jwtExpirationMs;

    /**
     * Genera la chiave crittografica HMAC basata sul segreto configurato.
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Crea un nuovo token JWT firmato includendo l'email (come subject), i ruoli dell'utente e la data di scadenza.
     */
    public String generateToken(String email, List<String> ruoli) {
        return Jwts.builder()
                .setSubject(email)
                .claim("ruoli", ruoli)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Decodifica il token per estrarne l'email (subject).
     */
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Verifica che il token sia autentico, correttamente firmato e non scaduto, gestendo le relative eccezioni.
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Token JWT non valido: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT scaduto: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT non supportato: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Payload del token JWT vuoto: {}", e.getMessage());
        }
        return false;
    }
}