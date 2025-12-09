package it.unicam.cs.ids2425.FilieraAgricola.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO (Data Transfer Object) generico per inviare messaggi di risposta al
 * client.
 * <p>
 * Viene utilizzato principalmente per confermare il successo di un'operazione
 * o per inviare messaggi informativi semplici (es. "Registrazione avvenuta con
 * successo").
 */
@Data
@AllArgsConstructor
public class MessageResponse {

    /**
     * Il contenuto testuale del messaggio da inviare al client.
     */
    private String message;
}
