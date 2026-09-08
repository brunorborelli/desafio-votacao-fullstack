package com.brunoborelli.votacao.dto;

import com.brunoborelli.votacao.entity.EscolhaVoto;
import com.brunoborelli.votacao.entity.Voto;

import java.time.Instant;

public record VotoResposta(
    Long id,
    Long pautaId,
    String cpfAssociado,
    EscolhaVoto escolha,
    Instant dataCriacao
) {

    public static VotoResposta de(Voto voto) {
        return new VotoResposta(
            voto.getId(),
            voto.getPauta().getId(),
            voto.getCpfAssociado(),
            voto.getEscolha(),
            voto.getDataCriacao()
        );
    }
}