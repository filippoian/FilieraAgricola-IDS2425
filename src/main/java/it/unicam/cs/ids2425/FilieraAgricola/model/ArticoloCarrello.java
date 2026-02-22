package it.unicam.cs.ids2425.FilieraAgricola.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Rappresenta un singolo articolo all'interno di un carrello.
 * Collega un prodotto specifico al carrello dell'utente definendo la quantità
 * desiderata.
 */
@Entity
@Data
@NoArgsConstructor
public class ArticoloCarrello {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Il carrello a cui appartiene questo articolo.
     * La relazione è Many-to-One: molti articoli possono appartenere allo stesso
     * carrello.
     *
     * @JsonIgnore viene utilizzato per evitare cicli infiniti durante la
     *             serializzazione JSON
     *             (Carrello -> Articoli -> Carrello -> ...).
     */
    @ManyToOne
    @JoinColumn(name = "carrello_id", nullable = false)
    @JsonIgnore
    private Carrello carrello;

    /**
     * L'articolo del marketplace associato a questo articolo del carrello
     * (opzionale se è un pacchetto).
     */
    @ManyToOne
    @JoinColumn(name = "marketplace_item_id", nullable = true)
    private MarketplaceItem marketplaceItem;

    /**
     * Il pacchetto associato a questo articolo del carrello (opzionale se è un
     * marketplace item).
     */
    @ManyToOne
    @JoinColumn(name = "pacchetto_id", nullable = true)
    private Pacchetto pacchetto;

    /**
     * La quantità del prodotto che l'utente intende acquistare.
     * Deve essere un valore positivo (gestito logicamente).
     */
    @Column(nullable = false)
    private int quantita;

    /**
     * Costruttore per creare un nuovo articolo da aggiungere al carrello
     * (Marketplace Item).
     */
    public ArticoloCarrello(Carrello carrello, MarketplaceItem marketplaceItem, int quantita) {
        this.carrello = carrello;
        this.marketplaceItem = marketplaceItem;
        this.pacchetto = null;
        this.quantita = quantita;
    }

    /**
     * Costruttore per creare un nuovo pacchetto da aggiungere al carrello.
     */
    public ArticoloCarrello(Carrello carrello, Pacchetto pacchetto, int quantita) {
        this.carrello = carrello;
        this.marketplaceItem = null;
        this.pacchetto = pacchetto;
        this.quantita = quantita;
    }
}