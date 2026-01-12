package it.unicam.cs.ids2425.FilieraAgricola.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Rappresenta l'associazione tra un {@link Pacchetto} e un
 * {@link MarketplaceItem}.
 * <p>
 * <strong>Cosa è:</strong>
 * È un'entità di giunzione che realizza una relazione molti-a-molti con
 * attributi (in questo caso, la quantità).
 * Collega uno specifico articolo presente nel marketplace a un pacchetto che lo
 * contiene.
 * <p>
 * <strong>Perché esiste:</strong>
 * Un pacchetto può contenere più istanze dello stesso articolo (es. 5 mele) o
 * articoli diversi.
 * Questa classe permette di definire <em>quanto</em> di ogni prodotto è incluso
 * nel pacchetto,
 * risolvendo la complessità della composizione dei bundle.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "marketplace_item_pacchetto")
public class MarketplaceItemPacchetto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marketplace_item_id", nullable = false)
    private MarketplaceItem item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pacchetto_id", nullable = false)
    private Pacchetto pacchetto;

    @Column(nullable = false)
    private Integer quantita = 1;
}
