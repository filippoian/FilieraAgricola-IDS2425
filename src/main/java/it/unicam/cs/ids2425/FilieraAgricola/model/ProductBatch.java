package it.unicam.cs.ids2425.FilieraAgricola.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode; // <--- FONDAMENTALE
import lombok.NoArgsConstructor;
import lombok.ToString; // <--- FONDAMENTALE
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class ProductBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Prodotto prodotto;

    @Column(nullable = false)
    private LocalDate dataProduzione;

    private double quantitaIniziale;

    @Column(nullable = false, unique = true)
    private String codiceLottoUnivoco;

    // --- INTERRUZIONE LOOP QUI SOTTO ---

    @OneToMany(mappedBy = "inputBatch")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<BatchInputLink> lottiOutput = new HashSet<>();

    @OneToMany(mappedBy = "outputBatch", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<BatchInputLink> lottiInput = new HashSet<>();

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<TraceabilityStep> steps = new HashSet<>();
}