package br.fiap.citizentech.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Idoso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    // Identifica o Arduino/ESP32 instalado na casa do idoso
    @Column(nullable = false, unique = true, length = 30)
    private String codigoDispositivo;

    // LAZY: nao carrega o cuidador a cada consulta de idoso
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuidador_id")
    private Cuidador cuidador;

    @OneToMany(mappedBy = "idoso", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Evento> eventos = new ArrayList<>();

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getCodigoDispositivo() { return codigoDispositivo; }
    public void setCodigoDispositivo(String codigoDispositivo) { this.codigoDispositivo = codigoDispositivo; }
    public Cuidador getCuidador() { return cuidador; }
    public void setCuidador(Cuidador cuidador) { this.cuidador = cuidador; }
    public List<Evento> getEventos() { return eventos; }
}
