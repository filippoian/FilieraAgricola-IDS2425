package it.unicam.cs.ids2425.FilieraAgricola.service.pricing;

import it.unicam.cs.ids2425.FilieraAgricola.model.OrderLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Factory per la creazione e la selezione della strategia di prezzo
 * ({@link PricingStrategy}) appropriata.
 * <p>
 * <strong>Cosa viene fatto:</strong>
 * Questa classe agisce come un punto centrale di decisione per determinare
 * quale logica di calcolo del prezzo
 * applicare a una data riga d'ordine.
 * <p>
 * <strong>Perché viene fatto:</strong>
 * Utilizzare il pattern Factory permette di disaccoppiare la logica di business
 * (il calcolo del totale)
 * dalla logica di selezione dell'algoritmo. Il chiamante (es. il servizio o il
 * controller) non deve sapere
 * se sta trattando un articolo singolo o un pacchetto; chiede semplicemente
 * alla factory la strategia corretta
 * e la esegue. Questo favorisce la manutenibilità e l'estendibilità del codice
 * (Open/Closed Principle).
 */
@Service
public class PricingStrategyFactory {

    private final PricingStrategy singleItemStrategy;
    private final PricingStrategy packageStrategy;

    @Autowired
    public PricingStrategyFactory(
            // Inietta la strategia specifica usando il @Qualifier
            @Qualifier("singleItemPricingStrategy") PricingStrategy singleItemStrategy,
            @Qualifier("packagePricingStrategy") PricingStrategy packageStrategy) {
        this.singleItemStrategy = singleItemStrategy;
        this.packageStrategy = packageStrategy;
    }

    /**
     * Seleziona e restituisce la strategia di prezzo corretta in base al contenuto
     * della {@link OrderLine}.
     * <p>
     * <strong>Cosa succede:</strong>
     * Il metodo ispeziona l'oggetto {@code OrderLine} passato come parametro.
     * <ul>
     * <li>Se la riga contiene un riferimento a un {@code MarketplaceItem},
     * restituisce la {@code singleItemStrategy}.</li>
     * <li>Se la riga contiene un riferimento a un {@code Pacchetto}, restituisce la
     * {@code packageStrategy}.</li>
     * </ul>
     * <p>
     * <strong>Perché viene fatto:</strong>
     * La natura dell'elemento venduto (singolo o pacchetto) determina regole di
     * prezzo potenzialmente diverse.
     * Centralizzando questa scelta qui, evitiamo di dover disseminare controlli
     * {@code if-else} o {@code instanceof}
     * nel resto del codice di servizio. Assicura che venga sempre applicata la
     * logica pertinente al tipo di dato presente.
     *
     * @param line La riga d'ordine da valutare.
     * @return L'istanza di {@link PricingStrategy} idonea per la riga fornita.
     * @throws IllegalArgumentException Se la riga d'ordine non contiene né un item
     *                                  né un pacchetto validi.
     */
    public PricingStrategy getStrategy(OrderLine line) {
        // Se la linea ha un MarketplaceItem, usa la strategia per item singoli
        if (line.getItem() != null) {
            return singleItemStrategy;
        }
        // Se la linea ha un Pacchetto, usa la strategia per pacchetti
        if (line.getPacchetto() != null) {
            return packageStrategy;
        }
        // Se non ha nessuno dei due, è un errore
        throw new IllegalArgumentException("OrderLine non valida: nessun item o pacchetto associato.");
    }
}
