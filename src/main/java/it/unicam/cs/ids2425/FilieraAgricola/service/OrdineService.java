package it.unicam.cs.ids2425.FilieraAgricola.service;

import it.unicam.cs.ids2425.FilieraAgricola.dto.request.CarrelloCheckoutDTO;
import it.unicam.cs.ids2425.FilieraAgricola.dto.response.OrdineResponse;
import it.unicam.cs.ids2425.FilieraAgricola.model.*;
import it.unicam.cs.ids2425.FilieraAgricola.repository.*;
import it.unicam.cs.ids2425.FilieraAgricola.service.pricing.PricingStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service per la gestione degli ordini d'acquisto.
 * <p>
 * <strong>Cosa fa:</strong>
 * Si occupa di tradurre un carrello (checkout) in un ordine effettivo salvato
 * nel database.
 * Gestisce il recupero dei dati, la creazione delle righe d'ordine
 * ({@link OrderLine})
 * e il calcolo del totale utilizzando il {@link PricingStrategyFactory}.
 * <p>
 * <strong>Workflow:</strong>
 * <ol>
 * <li>Riceve un DTO di checkout contenente liste di ID (item e pacchetti) e
 * quantità.</li>
 * <li>Recupera le entità corrispondenti dal database.</li>
 * <li>Crea per ciascuna entrata una {@code OrderLine}, "congelando" il prezzo
 * attuale.</li>
 * <li>Calcola il totale progressivo delegando alla Factory dei prezzi.</li>
 * <li>Salva l'ordine completo con tutte le sue righe.</li>
 * </ol>
 */
@Service
@RequiredArgsConstructor
public class OrdineService {

    private final OrdineRepository ordineRepository;
    private final UtenteRepository utenteRepository;
    private final MarketplaceItemRepository marketplaceItemRepository;
    private final PacchettoRepository pacchettoRepository;
    private final CarrelloRepository carrelloRepository;
    private final PricingStrategyFactory pricingStrategyFactory;

    /**
     * Trasforma i dati provenienti dal carrello (frontend) in un Ordine
     * persistente.
     * <p>
     * <strong>Cosa succede:</strong>
     * Il metodo itera separatamente sugli articoli singoli e sui pacchetti presenti
     * nel DTO.
     * Per ogni elemento:
     * <ul>
     * <li>Recupera l'entità dal DB.</li>
     * <li>Istanzia una {@link OrderLine} associandola all'ordine in creazione.</li>
     * <li>Imposta il prezzo d'acquisto corrente (snapshot storico).</li>
     * <li>Usa {@link PricingStrategyFactory} per calcolare il subtotale della riga
     * in base al tipo (Item o Pacchetto).</li>
     * </ul>
     * Infine, somma i subtotali nel totale dell'ordine e salva tutto.
     *
     * @param utenteId ID dell'acquirente che sta effettuando l'ordine.
     * @param request  DTO contenente gli articoli e i pacchetti selezionati nel
     *                 carrello.
     * @return DTO dell'ordine appena creato e salvato.
     * @throws RuntimeException Se l'utente, un articolo o un pacchetto non
     *                          esistono.
     */
    @Transactional
    public OrdineResponse creaOrdineDaCheckout(Long utenteId, CarrelloCheckoutDTO request) {
        Utente acquirente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new RuntimeException("Acquirente non trovato con id: " + utenteId));

        Ordine nuovoOrdine = new Ordine();
        nuovoOrdine.setAcquirente(acquirente);

        BigDecimal totaleOrdine = BigDecimal.ZERO;

        // Itera sugli ARTICOLI SINGOLI nel carrello
        for (CarrelloCheckoutDTO.Item item : request.getItems()) {
            MarketplaceItem marketplaceItem = marketplaceItemRepository.findById(item.getId())
                    .orElseThrow(() -> new RuntimeException("Articolo non trovato: " + item.getId()));

            BigDecimal prezzoDiAcquisto = BigDecimal.valueOf(marketplaceItem.getPrezzoUnitario());

            // Aggiunto 'null' come primo argomento per il campo 'id'
            OrderLine linea = new OrderLine(
                    null, // <--- ID
                    nuovoOrdine,
                    marketplaceItem,
                    null,
                    item.getQuantita(),
                    prezzoDiAcquisto);

            BigDecimal subtotale = pricingStrategyFactory.getStrategy(linea).calculatePrice(linea);
            totaleOrdine = totaleOrdine.add(subtotale);

            nuovoOrdine.getLinee().add(linea);
        }

        // Itera sui PACCHETTI nel carrello
        for (CarrelloCheckoutDTO.Item item : request.getPacchetti()) {
            Pacchetto pacchetto = pacchettoRepository.findById(item.getId())
                    .orElseThrow(() -> new RuntimeException("Pacchetto non trovato: " + item.getId()));

            BigDecimal prezzoDiAcquisto = pacchetto.getPrezzo_totale();

            // Aggiunto 'null' come primo argomento per il campo 'id'
            OrderLine linea = new OrderLine(
                    null, // <--- ID
                    nuovoOrdine,
                    null,
                    pacchetto,
                    item.getQuantita(),
                    prezzoDiAcquisto);

            BigDecimal subtotale = pricingStrategyFactory.getStrategy(linea).calculatePrice(linea);
            totaleOrdine = totaleOrdine.add(subtotale);

            nuovoOrdine.getLinee().add(linea);
        }

        nuovoOrdine.setTotale(totaleOrdine);
        Ordine ordineSalvato = ordineRepository.save(nuovoOrdine);

        // Svuota il carrello dell'utente dopo il checkout
        carrelloRepository.findByUtenteId(utenteId).ifPresent(carrello -> {
            carrello.getArticoli().clear();
            carrelloRepository.save(carrello);
        });

        return new OrdineResponse(ordineSalvato);
    }

    /**
     * Recupera lo storico degli ordini effettuati da un utente specifico.
     *
     * @param id ID dell'acquirente.
     * @return Lista di ordini.
     */
    public List<OrdineResponse> getOrdiniByUtente(Long id) {
        return ordineRepository.findByAcquirenteId(id)
                .stream()
                .map(OrdineResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Modifica lo stato di avanzamento di un ordine (es. DA PAGATO a SPEDITO).
     *
     * @param idOrdine   ID dell'ordine.
     * @param nuovoStato Stringa rappresentante il valore dell'enum
     *                   {@link StatoOrdine}.
     * @throws RuntimeException Se l'ordine non esiste o lo stato non è valido.
     */
    @Transactional
    public void aggiornaStato(Long idOrdine, String nuovoStato) {
        Ordine ordine = ordineRepository.findById(idOrdine)
                .orElseThrow(() -> new RuntimeException("Ordine non trovato"));
        try {
            StatoOrdine stato = StatoOrdine.valueOf(nuovoStato.toUpperCase());
            ordine.setStato(stato);
            ordineRepository.save(ordine);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Stato ordine non valido: " + nuovoStato);
        }
    }
}