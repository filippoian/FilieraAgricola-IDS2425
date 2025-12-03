package it.unicam.cs.ids2425.FilieraAgricola.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Rappresenta il profilo specifico per un attore di tipo 'Distributore'
 * all'interno della piattaforma.
 *
 * Questa entita' arricchisce le informazioni base di un utente (gestite tramite
 * {@link UserProfile})
 * aggiungendo dettagli operativi, legali e logistici necessari per le attivita'
 * di distribuzione nella filiera agricola.
 * Il Distributore agisce come intermediario logistico, gestendo il trasporto e
 * lo stoccaggio dei prodotti
 * tra i produttori e gli altri nodi della filiera (es. trasformatori o punti
 * vendita).
 */
@Entity
@Data
@NoArgsConstructor
public class ActorProfile_Distributore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Riferimento al profilo utente generico associato a questo distributore.
     *
     * Questo campo stabilisce una relazione 1:1 con l'entita' {@link UserProfile},
     * collegando
     * le credenziali di accesso e i dati anagrafici comuni al ruolo specifico di
     * distributore.
     * Ogni distributore deve essere associato a un unico account utente registrato.
     */
    @OneToOne
    @JoinColumn(name = "user_profile_id", nullable = false, unique = true)
    private UserProfile userProfile;

    /**
     * Il punto della filiera (es. magazzino centrale, hub logistico) gestito da
     * questo distributore.
     *
     * Rappresenta la sede operativa fisica dove avvengono le operazioni di
     * stoccaggio, smistamento
     * o partenza delle merci. E' fondamentale per la tracciabilita' geografica dei
     * lotti gestiti.
     */
    @OneToOne
    @JoinColumn(name = "filiera_point_id", nullable = false, unique = true)
    private FilieraPoint filieraPoint;

    /**
     * La ragione sociale dell'azienda di distribuzione.
     * Identifica legalmente l'entita' commerciale che opera nella filiera,
     * utilizzata nei documenti ufficiali.
     */
    private String ragioneSociale;

    /**
     * La Partita IVA del distributore.
     * Dato fiscale obbligatorio per la fatturazione e la validazione delle
     * transazioni commerciali B2B
     * all'interno della piattaforma.
     */
    private String partitaIva;

    /**
     * Informazioni dettagliate e non strutturate sulle capacita' logistiche del
     * distributore.
     *
     * Questo campo puo' contenere dettagli descrittivi come:
     * - Composizione della flotta (es. numero di camion, veicoli refrigerati)
     * - Capacita' di stoccaggio (es. metri quadri, celle frigorifere disponibili)
     * - Zone geografiche coperte e tempi medi di consegna
     * - Certificazioni specifiche per il trasporto alimenti (es. HACCP)
     *
     * Memorizzato come {@Lob} (Large Object) per consentire l'inserimento di testi
     * lunghi.
     */
    @Lob
    private String infoLogistica;
}