package it.unicam.cs.ids2425.FilieraAgricola.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode; // <--- FONDAMENTALE
import lombok.NoArgsConstructor;
import lombok.ToString; // <--- FONDAMENTALE
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class TraceabilityStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- INTERRUZIONE LOOP QUI SOTTO ---
    @ManyToOne(optional = false)
    @JoinColumn(name = "batch_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ProductBatch batch;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private Utente utente;

    @ManyToOne
    @JoinColumn(name = "filiera_point_id")
    private FilieraPoint filieraPoint;

    @Column(nullable = false)
    private String azione;

    @Lob
    private String descrizione;

    @Column(nullable = false)
    private LocalDateTime dataStep;
}