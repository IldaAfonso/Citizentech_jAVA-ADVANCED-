package br.fiap.citizentech.model;

import jakarta.persistence.*;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String username;

    // sempre guardada com hash BCrypt, nunca em texto puro
    @Column(nullable = false, length = 100)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Papel papel;

    public Usuario() { }

    public Usuario(String username, String senha, Papel papel) {
        this.username = username;
        this.senha = senha;
        this.papel = papel;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getSenha() { return senha; }
    public Papel getPapel() { return papel; }
}
