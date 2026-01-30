package it.unicam.cs.ids2425.FilieraAgricola.exception;

import it.unicam.cs.ids2425.FilieraAgricola.dto.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gestisce le eccezioni di stato (es. "già sottomesso") e le
     * trasforma in un 400 Bad Request leggibile.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<MessageResponse> handleIllegalStateException(IllegalStateException ex) {
        // Log dell'errore per il debug
        ex.printStackTrace();
        // Restituisce un errore chiaro all'utente
        return new ResponseEntity<>(
                new MessageResponse(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }
}