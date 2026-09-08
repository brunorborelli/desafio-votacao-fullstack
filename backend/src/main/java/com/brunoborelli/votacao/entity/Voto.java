package com.brunoborelli.votacao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "voto")
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;

    @Column(name = "cpf_associado", nullable = false, length = 11)
    private String cpfAssociado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private EscolhaVoto escolha;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private Instant dataCriacao;

    protected Voto() {}

    public Voto(Pauta pauta, String cpfAssociado, EscolhaVoto escolha) {
        this(pauta, cpfAssociado, escolha, Instant.now());
    }

    public Voto(
            Pauta pauta,
            String cpfAssociado,
            EscolhaVoto escolha,
            Instant dataCriacao
    ) {
        this.pauta = pauta;
        this.cpfAssociado = cpfAssociado;
        this.escolha = escolha;
        this.dataCriacao = dataCriacao;
    }

    public Long getId() {
        return id;
    }

    public Pauta getPauta() {
        return pauta;
    }

    public String getCpfAssociado() {
        return cpfAssociado;
    }

    public EscolhaVoto getEscolha() {
        return escolha;
    }

    public Instant getDataCriacao() {
        return dataCriacao;
    }
}