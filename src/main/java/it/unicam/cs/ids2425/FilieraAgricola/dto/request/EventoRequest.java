package it.unicam.cs.ids2425.FilieraAgricola.dto.request;

import lombok.Data;
import java.math.BigDecimal; // <--- Import
import java.time.LocalDate;

@Data
public class EventoRequest {
    private String nome;
    private String descrizione;
    private LocalDate data;
    private String luogo;

    // --- CAMPI AGGIUNTI ---
    private BigDecimal latitudine;
    private BigDecimal longitudine;
    // ----------------------

    private Long organizzatoreId;
}