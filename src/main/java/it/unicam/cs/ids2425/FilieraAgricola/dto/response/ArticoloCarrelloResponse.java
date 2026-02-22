package it.unicam.cs.ids2425.FilieraAgricola.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArticoloCarrelloResponse {
    private Long marketplaceItemId;
    private Long pacchettoId;
    private String nomeProdotto;
    private double prezzoUnitario;
    private int quantita;
}