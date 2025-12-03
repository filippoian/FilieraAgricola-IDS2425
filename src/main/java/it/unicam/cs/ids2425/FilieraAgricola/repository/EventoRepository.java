package it.unicam.cs.ids2425.FilieraAgricola.repository;

import it.unicam.cs.ids2425.FilieraAgricola.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository per la gestione degli eventi promozionali o educativi.
 *
 * Questa interfaccia permette di salvare e recuperare gli eventi organizzati
 * dagli attori della filiera
 * (es. degustazioni, visite in fattoria). Gli eventi servono a promuovere i
 * prodotti e ad avvicinare
 * i consumatori alla realta' produttiva locale.
 */
public interface EventoRepository extends JpaRepository<Evento, Long> {
}
