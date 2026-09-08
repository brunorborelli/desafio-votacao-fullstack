package com.brunoborelli.votacao.dto;

import com.brunoborelli.votacao.entity.SessaoVotacao;

import java.time.Instant;

public record SessaoVotacaoResposta(
    Long id,
    Long pautaId,
    Instant dataInicio,
    Instant dataFim
) {

    public static SessaoVotacaoResposta de(SessaoVotacao sessaoVotacao) {
        return new SessaoVotacaoResposta(
            sessaoVotacao.getId(),
            sessaoVotacao.getPauta().getId(),
            sessaoVotacao.getDataInicio(),
            sessaoVotacao.getDataFim()
        );
    }
}