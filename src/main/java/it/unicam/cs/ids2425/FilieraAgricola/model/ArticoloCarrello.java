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
     * Il prodotto associato a questo articolo del carrello.
     * Rappresenta cosa l'utente sta acquistando.
     */
    @ManyToOne
    @JoinColumn(name = "prodotto_id", nullable = false)
    private Prodotto prodotto;

    /**
     * La quantità del prodotto che l'utente intende acquistare.
     * Deve essere un valore positivo (gestito logicamente).
     */
    @Column(nullable = false)
    private int quantita;

    /**
     * Costruttore per creare un nuovo articolo da aggiungere al carrello.
     *
     * @param carrello Il carrello di destinazione.
     * @param prodotto Il prodotto selezionato.
     * @param quantita La quantità desiderata.
     */
    public ArticoloCarrello(Carrello carrello, Prodotto prodotto, int quantita) {
        this.carrello = carrello;
        this.prodotto = prodotto;
        this.quantita = quantita;
    }
}