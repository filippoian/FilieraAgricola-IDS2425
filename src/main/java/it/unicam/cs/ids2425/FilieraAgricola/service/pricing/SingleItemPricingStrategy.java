package it.unicam.cs.ids2425.FilieraAgricola.service.pricing;

import it.unicam.cs.ids2425.FilieraAgricola.model.OrderLine;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Questa classe implementa l'interfaccia {@link PricingStrategy} e definisce la
 * strategia di calcolo
 * del prezzo per gli articoli singoli (MarketplaceItem) presenti in una riga
 * d'ordine.
 * <p>
 * Viene utilizzata quando una {@link OrderLine} si riferisce a un singolo
 * prodotto, piuttosto che a un pacchetto
 * o a un'altra tipologia di elemento vendibile.
 */
@Service("singleItemPricingStrategy") // Nome del bean per la Factory
public class SingleItemPricingStrategy implements PricingStrategy {

    /**
     * Calcola il prezzo totale per la riga d'ordine passata come parametro,
     * specifica per un singolo item.
     * <p>
     * <strong>Cosa succede:</strong>
     * Il metodo verifica prima che la riga d'ordine contenga effettivamente un item
     * singolo. Successivamente,
     * recupera e restituisce il valore del sottototale già presente nell'oggetto
     * {@code OrderLine}.
     * <p>
     * <strong>Perché viene fatto così:</strong>
     * La logica di calcolo del prezzo (prezzo unitario * quantità) viene
     * "congelata" ed eseguita al momento
     * della creazione o dell'aggiornamento della riga d'ordine. In questa fase, la
     * strategia si limita a
     * restituire quel valore. Questo approccio è fondamentale per garantire che il
     * prezzo storico di un ordine
     * rimanga immutato anche se il venditore dovesse modificare il prezzo base
     * dell'articolo in futuro.
     *
     * @param line La riga d'ordine di cui calcolare il prezzo. Deve contenere un
     *             riferimento valido a un item.
     * @return Il prezzo totale per la riga (corrispondente al subtotale
     *         memorizzato).
     * @throws IllegalArgumentException Se la riga d'ordine non contiene un item
     *                                  valido (è null).
     */
    @Override
    public BigDecimal calculatePrice(OrderLine line) {
        if (line.getItem() == null) {
            throw new IllegalArgumentException("Strategy errata: OrderLine non è un item singolo.");
        }
        // La logica è già "congelata" nell'entità OrderLine
        // per garantire che il prezzo non cambi dopo l'acquisto.
        return line.getSubtotale();
    }
}
