package it.unicam.cs.ids2425.FilieraAgricola.dto.response;

import it.unicam.cs.ids2425.FilieraAgricola.model.Evento;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EventoResponse {
    private Long id;
    private String nome;
    private String descrizione;
    private LocalDate data;
    private String luogo;
    private BigDecimal latitudine;
    private BigDecimal longitudine;

    // --- NUOVO CAMPO ---
    private String nomeOrganizzatore;
    // -------------------

    public EventoResponse(Evento evento) {
        this.id = evento.getId();
        this.nome = evento.getNome();
        this.descrizione = evento.getDescrizione();
        this.data = evento.getData();
        this.luogo = evento.getLuogo();
        this.latitudine = evento.getLatitudine();
        this.longitudine = evento.getLongitudine();

        // Popola il nome dell'organizzatore
        if (evento.getOrganizzatore() != null) {
            if (evento.getOrganizzatore().getUserProfile() != null) {
                this.nomeOrganizzatore = evento.getOrganizzatore().getUserProfile().getNome() + " " +
                        evento.getOrganizzatore().getUserProfile().getCognome();
            } else {
                this.nomeOrganizzatore = evento.getOrganizzatore().getEmail();
            }
        } else {
            this.nomeOrganizzatore = "Sconosciuto";
        }
    }
}