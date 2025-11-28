package it.unicam.cs.ids2425.FilieraAgricola.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Rappresenta un articolo messo in vendita nel marketplace.
 * Collega un prodotto a un venditore, specificando prezzo, quantità disponibile
 * e unità di misura.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketplaceItem {

    /**
     * Identificativo univoco dell'articolo nel marketplace.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Il prodotto messo in vendita.
     */
    @ManyToOne
    @JoinColumn(name = "prodotto_id", nullable = false)
    private Prodotto prodotto;

    /**
     * L'utente che vende l'articolo.
     */
    @ManyToOne
    @JoinColumn(name = "seller_user_id", nullable = false)
    private Utente venditore;

    /**
     * Il prezzo per singola unità di misura.
     */
    @Column(nullable = false)
    private double prezzoUnitario;

    /**
     * L'unità di misura utilizzata per la vendita (es. KG, PZ, L).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitaDiMisura unitaDiMisura;

    /**
     * La quantità attualmente disponibile in stock.
     */
    @Column(nullable = false)
    private int stockDisponibile;
}
