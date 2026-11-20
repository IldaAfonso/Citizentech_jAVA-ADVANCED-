package br.fiap.citizentech.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Cuidador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Column(length = 120)
    private String email;

    // CASCADE ALL + orphanRemoval: apagar o cuidador apaga seus idosos (e, em cascata, os eventos deles).
    // LAZY: os idosos so sao carregados quando alguem pede.
    @OneToMany(mappedBy = "cuidador", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Idoso> idosos = new ArrayList<>();

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Idoso> getIdosos() { return idosos; }
}
