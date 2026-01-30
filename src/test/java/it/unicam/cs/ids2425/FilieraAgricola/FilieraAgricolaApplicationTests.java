
package it.unicam.cs.ids2425.FilieraAgricola;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// Verifica che l'intero contesto dell'applicazione Spring Boot si carichi correttamente.
@SpringBootTest
// Utilizziamo il profilo di test per caricare le configurazioni specifiche (es.
// database in memoria)
// definite in application-test.properties.
@ActiveProfiles("test")

class FilieraAgricolaApplicationTests {

	@Test
	void contextLoads() {
		// Se il metodo non lancia eccezioni, significa che i bean principali e la
		// configurazione
		// del database sono stati inizializzati correttamente.
	}

}