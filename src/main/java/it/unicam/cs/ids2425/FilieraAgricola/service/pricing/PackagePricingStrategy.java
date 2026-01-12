package it.unicam.cs.ids2425.FilieraAgricola.service.pricing;

import it.unicam.cs.ids2425.FilieraAgricola.model.OrderLine;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Questa classe implementa l'interfaccia {@link PricingStrategy} e definisce la
 * strategia di calcolo
 * del prezzo per i pacchetti (contenitori di più articoli) presenti in una riga
 * d'ordine.
 * <p>
 * Viene utilizzata quando una {@link OrderLine} si riferisce specificamente a
 * un pacchetto promozionale o raggruppato,
 * distinguendosi dalla vendita di articoli singoli.
 */
@Service("packagePricingStrategy") // Nome del bean per la Factory
public class PackagePricingStrategy implements PricingStrategy {

    /**
     * Calcola il prezzo totale per la riga d'ordine passata come parametro,
     * specifica per un pacchetto.
     * <p>
     * <strong>Cosa succede:</strong>
     * Il metodo verifica prima che la riga d'ordine contenga effettivamente un
     * pacchetto. Successivamente,
     * restituisce il valore del sottototale già calcolato e memorizzato
     * nell'oggetto {@code OrderLine}.
     * <p>
     * <strong>Perché viene fatto così:</strong>
     * Il prezzo di un pacchetto è spesso forfettario o soggetto a promozioni
     * specifiche. La logica che determina
     * questo valore è "congelata" nell'entità `OrderLine` al momento della
     * creazione dell'ordine.
     * In questo modo, restituiamo semplicemente il valore storico, garantendo che
     * le modifiche future al prezzo
     * o alla composizione del pacchetto non alterino retroattivamente gli ordini
     * già effettuati.
     *
     * @param line La riga d'ordine di cui calcolare il prezzo. Deve contenere un
     *             riferimento valido a un pacchetto.
     * @return Il prezzo totale per la riga (corrispondente al subtotale
     *         memorizzato).
     * @throws IllegalArgumentException Se la riga d'ordine non contiene un
     *                                  pacchetto valido (è null).
     */
    @Override
    public BigDecimal calculatePrice(OrderLine line) {
        if (line.getPacchetto() == null) {
            throw new IllegalArgumentException("Strategy errata: OrderLine non è un pacchetto.");
        }
        // Anche qui, la logica è già "congelata" nell'entità OrderLine.
        // Il prezzo è forfettario per il pacchetto.
        return line.getSubtotale();
    }
}
