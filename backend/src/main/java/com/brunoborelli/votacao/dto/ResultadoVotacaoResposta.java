package com.brunoborelli.votacao.dto;

import com.brunoborelli.votacao.entity.SituacaoResultadoVotacao;

public record ResultadoVotacaoResposta(
    Long pautaId,
    String titulo,
    long quantidadeVotosSim,
    long quantidadeVotosNao,
    long totalVotos,
    SituacaoResultadoVotacao resultado
) {
}