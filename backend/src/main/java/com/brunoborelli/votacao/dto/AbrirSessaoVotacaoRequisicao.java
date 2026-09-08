package com.brunoborelli.votacao.dto;

import jakarta.validation.constraints.Positive;

public record AbrirSessaoVotacaoRequisicao(
    @Positive(message = "A duração deve ser maior que zero")
    Integer duracaoMinutos
) {
}