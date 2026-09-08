package com.brunoborelli.votacao.dto;

import com.brunoborelli.votacao.entity.Pauta;

import java.time.Instant;

public record PautaResposta(
    Long id,
    String titulo,
    String descricao,
    Instant dataCriacao
) {

    public static PautaResposta de(Pauta pauta) {
        return new PautaResposta(
            pauta.getId(),
            pauta.getTitulo(),
            pauta.getDescricao(),
            pauta.getDataCriacao()
        );
    }
}