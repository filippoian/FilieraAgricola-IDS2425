package it.unicam.cs.ids2425.FilieraAgricola.security;

import it.unicam.cs.ids2425.FilieraAgricola.model.Utente;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    // --- CAMPO AGGIUNTO ---
    private boolean enabled;

    // --- COSTRUTTORE MODIFICATO ---
    public UserDetailsImpl(Long id, String email, String password,
                           Collection<? extends GrantedAuthority> authorities,
                           boolean enabled) { // Aggiunto 'enabled'
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.enabled = enabled; // Impostato 'enabled'
    }

    // --- METODO 'build' MODIFICATO ---
    public static UserDetailsImpl build(Utente utente) {

        List<GrantedAuthority> authorities = utente.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                utente.getId(),
                utente.getEmail(),
                utente.getPassword(),
                authorities,
                utente.isEnabled() // Passa il flag 'enabled' dal DB
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // --- METODO 'isEnabled' MODIFICATO ---
    @Override
    public boolean isEnabled() {
        // Restituisce lo stato di abilitazione reale dell'utente
        return this.enabled;
    }
}