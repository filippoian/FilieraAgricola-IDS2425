package it.unicam.cs.ids2425.FilieraAgricola.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode; // <--- Aggiunto per sicurezza (come per le altre entità)
import lombok.NoArgsConstructor;
import lombok.ToString;       // <--- Aggiunto

import java.math.BigDecimal; // <--- IMPORTANTE
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Lob
    private String descrizione;

    @Column(nullable = false)
    private LocalDate data;

    private String luogo;

    // --- CAMPI AGGIUNTI PER LA MAPPA ---
    @Column(precision = 10, scale = 7)
    private BigDecimal latitudine;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitudine;
    // ----------------------------------

    @ManyToOne
    @JoinColumn(name = "organizzatore_id")
    @ToString.Exclude          // <--- Già che ci siamo, proteggiamo anche qui dai loop
    @EqualsAndHashCode.Exclude
    private Utente organizzatore;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "submission_id")
    private ContentSubmission submission;
}