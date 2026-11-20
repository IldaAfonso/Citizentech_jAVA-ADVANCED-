package br.fiap.citizentech.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoEvento tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusEvento status = StatusEvento.PENDENTE;

    @Column(nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    @Column(length = 200)
    private String observacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idoso_id")
    private Idoso idoso;

    public Long getId() { return id; }
    public TipoEvento getTipo() { return tipo; }
    public void setTipo(TipoEvento tipo) { this.tipo = tipo; }
    public StatusEvento getStatus() { return status; }
    public void setStatus(StatusEvento status) { this.status = status; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public Idoso getIdoso() { return idoso; }
    public void setIdoso(Idoso idoso) { this.idoso = idoso; }
}
