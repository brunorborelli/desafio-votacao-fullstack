package com.brunoborelli.votacao.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SituacaoResultadoVotacaoTest {

    @Test
    void deveDefinirPautaAprovada() {
        assertThat(SituacaoResultadoVotacao.de(2, 1))
            .isEqualTo(SituacaoResultadoVotacao.APROVADA);
    }

    @Test
    void deveDefinirPautaReprovada() {
        assertThat(SituacaoResultadoVotacao.de(1, 2))
            .isEqualTo(SituacaoResultadoVotacao.REPROVADA);
    }

    @Test
    void deveDefinirEmpate() {
        assertThat(SituacaoResultadoVotacao.de(1, 1))
            .isEqualTo(SituacaoResultadoVotacao.EMPATE);
    }

    @Test
    void deveDefinirAusenciaDeVotos() {
        assertThat(SituacaoResultadoVotacao.de(0, 0))
            .isEqualTo(SituacaoResultadoVotacao.SEM_VOTOS);
    }
}