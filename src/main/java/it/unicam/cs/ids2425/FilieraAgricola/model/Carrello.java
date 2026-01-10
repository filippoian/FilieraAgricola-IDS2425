package it.unicam.cs.ids2425.FilieraAgricola.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta il carrello della spesa di un utente.
 * Contiene la lista degli articoli che l'utente intende acquistare.
 * Ogni utente possiede un solo carrello (relazione One-to-One).
 */
@Entity
@Data
@NoArgsConstructor
public class Carrello {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * L'utente proprietario del carrello.
     * La relazione è obbligatoria (nullable = false).
     */
    @OneToOne
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;

    /**
     * Lista degli articoli contenuti nel carrello.
     *
     * CascadeType.ALL: Le operazioni sul carrello si propagano agli articoli.
     * orphanRemoval = true: Se un articolo viene rimosso dalla lista, viene
     * eliminato dal database.
     * FetchType.EAGER: Gli articoli vengono caricati immediatamente insieme al
     * carrello.
     */
    @OneToMany(mappedBy = "carrello", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ArticoloCarrello> articoli = new ArrayList<>();

    /**
     * Costruttore che associa il carrello a un utente specifico.
     *
     * @param utente L'utente a cui appartiene il carrello.
     */
    public Carrello(Utente utente) {
        this.utente = utente;
    }
}