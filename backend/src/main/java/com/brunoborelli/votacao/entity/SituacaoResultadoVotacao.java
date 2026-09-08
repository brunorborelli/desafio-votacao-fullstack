package com.brunoborelli.votacao.entity;

public enum SituacaoResultadoVotacao {
    APROVADA,
    REPROVADA,
    EMPATE,
    SEM_VOTOS;

    public static SituacaoResultadoVotacao de(long quantidadeSim, long quantidadeNao) {
        if (quantidadeSim == 0 && quantidadeNao == 0) {
            return SEM_VOTOS;
        }

        if (quantidadeSim > quantidadeNao) {
            return APROVADA;
        }

        if (quantidadeNao > quantidadeSim) {
            return REPROVADA;
        }

        return EMPATE;
    }
}