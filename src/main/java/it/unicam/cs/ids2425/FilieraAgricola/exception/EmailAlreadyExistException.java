package it.unicam.cs.ids2425.FilieraAgricola.exception;

/**
 * Eccezione rilanciata quando si tenta di registrare un utente con un'email già
 * presente a sistema.
 */
public class EmailAlreadyExistException extends RuntimeException {
    /**
     * Crea una nuova eccezione con il messaggio specificato.
     * 
     * @param message messaggio descrittivo dell'errore.
     */
    public EmailAlreadyExistException(String message) {
        super(message);
    }
}