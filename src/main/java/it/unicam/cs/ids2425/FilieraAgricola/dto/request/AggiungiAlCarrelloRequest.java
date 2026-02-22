package it.unicam.cs.ids2425.FilieraAgricola.dto.request;

import lombok.Data;

@Data
public class AggiungiAlCarrelloRequest {
    private Long marketplaceItemId;
    private Long pacchettoId;
    private int quantita;
}