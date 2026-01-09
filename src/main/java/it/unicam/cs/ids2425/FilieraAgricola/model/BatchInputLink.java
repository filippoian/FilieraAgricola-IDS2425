package it.unicam.cs.ids2425.FilieraAgricola.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode; // <--- FONDAMENTALE
import lombok.NoArgsConstructor;
import lombok.ToString; // <--- FONDAMENTALE
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
public class BatchInputLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- INTERRUZIONE LOOP QUI SOTTO ---
    @ManyToOne(optional = false)
    @JoinColumn(name = "output_batch_id")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ProductBatch outputBatch;

    @ManyToOne(optional = false)
    @JoinColumn(name = "input_batch_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ProductBatch inputBatch;
}