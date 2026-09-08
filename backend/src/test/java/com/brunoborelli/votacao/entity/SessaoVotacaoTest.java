package com.brunoborelli.votacao.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class SessaoVotacaoTest {

    private static final Instant INICIO = Instant.parse("2026-01-01T12:00:00Z");
    private static final Instant FIM = Instant.parse("2026-01-01T12:01:00Z");

    private final SessaoVotacao sessao = new SessaoVotacao(
        new Pauta("Pauta", "Descrição da pauta"),
        INICIO,
        FIM
    );

    @Test
    void deveConsiderarSessaoAbertaNoInstanteInicial() {
        assertThat(sessao.estaAbertaEm(INICIO)).isTrue();
    }

    @Test
    void deveConsiderarSessaoAbertaAntesDoInstanteFinal() {
        assertThat(sessao.estaAbertaEm(FIM.minusNanos(1))).isTrue();
    }

    @Test
    void deveConsiderarSessaoEncerradaNoInstanteFinal() {
        assertThat(sessao.estaAbertaEm(FIM)).isFalse();
        assertThat(sessao.estaEncerradaEm(FIM)).isTrue();
    }

    @Test
    void deveConsiderarSessaoFechadaAntesDoInstanteInicial() {
        assertThat(sessao.estaAbertaEm(INICIO.minusNanos(1))).isFalse();
        assertThat(sessao.estaEncerradaEm(INICIO.minusNanos(1))).isFalse();
    }
}