package it.unicam.cs.ids2425.FilieraAgricola.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "utenti")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email; // Questo è l'username

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "utente_roles",
            joinColumns = @JoinColumn(name = "utente_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "utente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile userProfile;

    /**
     * Flag per l'approvazione del Gestore.
     * Di default è 'false'. I ruoli 'UTENTEGENERICO' e 'ACQUIRENTE'
     * lo impostano a 'true' al momento della registrazione.
     * Gli altri ruoli richiedono l'approvazione del Gestore per impostarlo a 'true'.
     */
    @Column(nullable = false)
    private boolean enabled = false;
}