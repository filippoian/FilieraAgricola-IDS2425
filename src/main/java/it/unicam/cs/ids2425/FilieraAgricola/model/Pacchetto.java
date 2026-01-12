package it.unicam.cs.ids2425.FilieraAgricola.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Rappresenta un pacchetto di prodotti (bundle) all'interno della piattaforma.
 * <p>
 * <strong>Cosa è:</strong>
 * Un'entità JPA che raggruppa diversi {@link MarketplaceItem} (tramite
 * {@link MarketplaceItemPacchetto})
 * per essere venduti insieme come un'unica unità commerciale.
 * <p>
 * <strong>Perché esiste:</strong>
 * Permette ai Distributori di creare offerte speciali, cesti regalo o
 * confezioni miste,
 * definendo un prezzo unico per l'insieme dei prodotti. Questa entità gestisce
 * i dati descrittivi,
 * il prezzo complessivo e il collegamento al distributore che lo ha creato.
 * <p>
 * Un pacchetto è soggetto ad approvazione tramite il meccanismo di
 * {@link ContentSubmission}.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pacchetto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Lob
    private String descrizione;

    @ManyToOne
    @JoinColumn(name = "distributore_id", nullable = false)
    private Utente distributore;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzo_totale;

    @OneToMany(mappedBy = "pacchetto", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<MarketplaceItemPacchetto> items = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "submission_id")
    private ContentSubmission submission;
}